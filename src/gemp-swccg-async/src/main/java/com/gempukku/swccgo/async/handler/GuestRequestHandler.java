package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.save.GameSaveHeader;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.guest.GuestLimits;
import com.gempukku.swccgo.guest.GuestSettings;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.hall.HallException;
import com.gempukku.swccgo.hall.HallServer;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Play now, under {@code /gemp-swccg-server/guest}:
 * <ul>
 * <li>{@code GET /start} (side, difficulty): swccg.com Start; mints a guest, starts a P-ANH precon vs the bot,
 * and 302s to game.html. {@code difficulty=beginner} is OzzelBot; {@code harder} (or {@code advanced}) is YodaBot.
 * {@code bot=ozzel|yoda} is also accepted. Chat is on.</li>
 * <li>{@code POST /start} (side, bot): starts a game for a guest or the logged-in player and returns its id as JSON.</li>
 * <li>{@code GET /status}: whether Play now is on, and the caller's unfinished games (in memory or saved).</li>
 * <li>{@code POST /resume} (gameId): puts one of the caller's games back in play.</li>
 * <li>{@code POST /mute} (gameId, muted): the player mutes or unmutes spectator chat in their game.</li>
 * <li>{@code GET /play?side=&bot=}: a shareable link; redirects to guest.html. Also accepts {@code difficulty=}.</li>
 * </ul>
 * Errors come back with a {@code message} header (shown to the player) and an {@code X-Guest-Error} code:
 * 503 {@code disabled} or {@code busy}, 429 {@code too_many_games} or {@code rate_limited} (with Retry-After),
 * 409 {@code incompatible} (a save from an older version that can't be continued),
 * 400 {@code bad_request}, 401, 404. None of these responses may be indexed.
 */
public class GuestRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    static final String ERROR_HEADER = "X-Guest-Error";
    private static final String NO_INDEX = "noindex, nofollow";
    private static final String SYSTEM = "System";

    private final HallServer _hallServer;
    private final SwccgoServer _swccgoServer;
    private final ChatServer _chatServer;
    private final GuestSettings _settings;
    private final GuestLimits _limits;

    public GuestRequestHandler(Map<Type, Object> context) {
        super(context);
        _hallServer = extractObject(context, HallServer.class);
        _swccgoServer = extractObject(context, SwccgoServer.class);
        _chatServer = extractObject(context, ChatServer.class);
        GuestSettings settings = extractObject(context, GuestSettings.class);
        _settings = settings != null ? settings : GuestSettings.defaults();
        _limits = new GuestLimits(_settings, System::currentTimeMillis);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        String path = new QueryStringDecoder(uri).path();
        if (path.equals("/start") && (request.method() == HttpMethod.POST || request.method() == HttpMethod.GET)) {
            start(request, responseWriter, remoteIp);
        } else if (path.equals("/status") && request.method() == HttpMethod.GET) {
            status(request, responseWriter);
        } else if (path.equals("/resume") && request.method() == HttpMethod.POST) {
            resume(request, responseWriter, remoteIp);
        } else if (path.equals("/mute") && request.method() == HttpMethod.POST) {
            mute(request, responseWriter);
        } else if (path.equals("/play") && request.method() == HttpMethod.GET) {
            play(request, responseWriter);
        } else {
            error(responseWriter, 404, "not_found", "Not found", 0);
        }
    }

    private void start(HttpRequest request, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (!_settings.enabled()) {
            error(responseWriter, 503, "disabled", "Play now is not available right now.", 0);
            return;
        }
        boolean redirectToGame = request.method() == HttpMethod.GET;
        PlayNowSide side;
        PlayNowBot bot;
        if (redirectToGame) {
            QueryStringDecoder query = new QueryStringDecoder(request.uri());
            side = PlayNowSide.fromCode(getQueryParameterSafely(query, "side"));
            bot = PlayNowBot.fromChoice(getQueryParameterSafely(query, "bot"),
                    getQueryParameterSafely(query, "difficulty"));
        } else {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
            try {
                side = PlayNowSide.fromCode(getFormParameterSafely(postDecoder, "side"));
                bot = PlayNowBot.fromChoice(getFormParameterSafely(postDecoder, "bot"),
                        getFormParameterSafely(postDecoder, "difficulty"));
            } finally {
                postDecoder.destroy();
            }
        }
        if (side == null || bot == null) {
            error(responseWriter, 400, "bad_request",
                    "Pick a side (light or dark) and beginner or harder.", 0);
            return;
        }

        String loggedUser = getLoggedUser(request);
        Player account = loggedUser != null ? _playerDao.getPlayer(loggedUser) : null;
        Map<String, String> headers = baseHeaders();
        String owner;
        if (account != null) {
            owner = account.getName();
        } else {
            String token = getCookieValue(request, GuestIdentity.COOKIE);
            if (!GuestIdentity.isToken(token))
                token = GuestIdentity.newToken();
            owner = GuestIdentity.guestIdFor(token);
            _limits.cleanup();
            GuestLimits.Decision decision = _limits.tryStart(remoteIp, _playNowGames.countGuestGamesFromIp(remoteIp));
            if (!decision.allowed()) {
                if ("too_many_games".equals(decision.reason()))
                    error(responseWriter, 429, "too_many_games", "You already have the most games going at once. Finish one first.", 0);
                else
                    error(responseWriter, 429, "rate_limited", "Too many games started. Please wait a little and try again.", decision.retryAfterSeconds());
                return;
            }
            headers.put(HttpHeaderNames.SET_COOKIE.toString(), encodeProtectedCookie(GuestIdentity.COOKIE, token,
                    GuestIdentity.COOKIE_DAYS * 24L * 60 * 60, CookieHeaderNames.SameSite.Lax));
        }

        String gameId;
        try {
            gameId = _hallServer.startPlayNowGame(owner, account, side, bot, remoteIp);
        } catch (HallServer.PlayNowBusyException exp) {
            error(responseWriter, 503, "busy", exp.getMessage(), 0);
            return;
        } catch (HallException exp) {
            error(responseWriter, 503, "unavailable", exp.getMessage(), 0);
            return;
        }
        String seatId = _playNowGames.get(gameId).getSeatId();
        if (redirectToGame) {
            headers.put("Location", gamePage(gameId, seatId, account == null));
            responseWriter.writeError(302, headers);
            return;
        }
        json(responseWriter, "{\"gameId\":" + quote(gameId)
                + ",\"participantId\":" + quote(seatId)
                + ",\"guest\":" + (account == null) + "}", headers);
    }

    /** Same path game.html uses for a Play now seat. */
    static String gamePage(String gameId, String participantId, boolean guest) {
        return "/gemp-swccg/game.html?gameId=" + encodeQuery(gameId)
                + "&participantId=" + encodeQuery(participantId)
                + "&playNow=1" + (guest ? "&guest=1" : "");
    }

    private static String encodeQuery(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private void status(HttpRequest request, ResponseWriter responseWriter) {
        String owner = currentOwner(request);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"enabled\":").append(_settings.enabled());
        sb.append(",\"loggedIn\":").append(getLoggedUser(request) != null);
        sb.append(",\"games\":[");
        if (owner != null) {
            boolean first = true;
            for (PlayNowGames.Entry game : _playNowGames.ownedBy(owner)) {
                SwccgGameMediator mediator = _swccgoServer != null ? _swccgoServer.getGameById(game.getGameId()) : null;
                if (mediator == null || mediator.isFinished() || mediator.isDestroyed())
                    continue;
                if (!first)
                    sb.append(',');
                first = false;
                appendGame(sb, game);
            }
            for (GameSaveHeader saved : _hallServer.getSavedPlayNowGames(owner)) {
                if (!first)
                    sb.append(',');
                first = false;
                appendSavedGame(sb, saved);
            }
        }
        sb.append("]}");
        json(responseWriter, sb.toString(), baseHeaders());
    }

    private static void appendSavedGame(StringBuilder sb, GameSaveHeader header) {
        String seat = null;
        String side = null;
        String bot = null;
        for (GameSaveHeader.Seat s : header.seats) {
            if (s.bot == null) {
                seat = s.playerId;
                PlayNowSide playNowSide = PlayNowSide.fromDeckName(s.deckName);
                side = playNowSide != null ? playNowSide.getCode() : null;
            } else {
                bot = s.bot;
            }
        }
        sb.append("{\"gameId\":").append(quote(header.gameId))
                .append(",\"participantId\":").append(quote(seat))
                .append(",\"side\":").append(quote(side))
                .append(",\"bot\":").append(quote(bot))
                .append(",\"saved\":true")
                .append(",\"turn\":").append(header.turn)
                .append(",\"lastPlayed\":").append(header.updatedAt)
                .append('}');
    }

    private void resume(HttpRequest request, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (!_settings.enabled()) {
            error(responseWriter, 503, "disabled", "Play now is not available right now.", 0);
            return;
        }
        String gameId;
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            gameId = getFormParameterSafely(postDecoder, "gameId");
        } finally {
            postDecoder.destroy();
        }
        String loggedUser = getLoggedUser(request);
        String owner = currentOwner(request);
        if (owner == null || gameId == null) {
            error(responseWriter, 404, "not_found", "Game not found.", 0);
            return;
        }
        boolean guest = loggedUser == null;
        PlayNowGames.Entry live = _playNowGames.get(gameId);
        if (guest && live == null && _playNowGames.countGuestGamesFromIp(remoteIp) >= _settings.maxConcurrentPerIp()
                && !_settings.ipAllowlist().contains(remoteIp)) {
            error(responseWriter, 429, "too_many_games", "You already have the most games going at once. Finish one first.", 0);
            return;
        }
        String resumed;
        try {
            resumed = _hallServer.resumePlayNowGame(owner, gameId, remoteIp);
        } catch (HallServer.PlayNowBusyException exp) {
            error(responseWriter, 503, "busy", exp.getMessage(), 0);
            return;
        } catch (HallServer.PlayNowIncompatibleException exp) {
            error(responseWriter, 409, "incompatible", exp.getMessage(), 0);
            return;
        } catch (HallException exp) {
            error(responseWriter, 503, "unavailable", exp.getMessage(), 0);
            return;
        }
        PlayNowGames.Entry game = resumed != null ? _playNowGames.get(resumed) : null;
        if (game == null) {
            error(responseWriter, 404, "not_found", "Game not found.", 0);
            return;
        }
        Map<String, String> headers = baseHeaders();
        if (guest) {
            // Each resume keeps the guest cookie alive as long as the save.
            headers.put(HttpHeaderNames.SET_COOKIE.toString(), encodeProtectedCookie(GuestIdentity.COOKIE,
                    getCookieValue(request, GuestIdentity.COOKIE), GuestIdentity.COOKIE_DAYS * 24L * 60 * 60, CookieHeaderNames.SameSite.Lax));
        }
        json(responseWriter, "{\"gameId\":" + quote(game.getGameId())
                + ",\"participantId\":" + quote(game.getSeatId())
                + ",\"guest\":" + guest + "}", headers);
    }

    static void appendGame(StringBuilder sb, PlayNowGames.Entry game) {
        sb.append("{\"gameId\":").append(quote(game.getGameId()))
                .append(",\"participantId\":").append(quote(game.getSeatId()))
                .append(",\"side\":").append(quote(game.getSide().getCode()))
                .append(",\"bot\":").append(quote(game.getBot().getCode()))
                .append(",\"chatMuted\":").append(game.isChatMuted())
                .append(",\"saved\":false")
                .append('}');
    }

    private void mute(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        String gameId;
        boolean muted;
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            gameId = getFormParameterSafely(postDecoder, "gameId");
            muted = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "muted"));
        } finally {
            postDecoder.destroy();
        }
        String owner = currentOwner(request);
        if (owner == null) {
            error(responseWriter, 401, "not_signed_in", "Not your game.", 0);
            return;
        }
        PlayNowGames.Entry game = _playNowGames.get(gameId);
        if (game == null || !game.getOwner().equals(owner)) {
            error(responseWriter, 404, "not_found", "Game not found.", 0);
            return;
        }
        if (game.isChatMuted() != muted) {
            game.setChatMuted(muted);
            ChatRoomMediator room = _chatServer != null ? _chatServer.getChatRoom(SwccgoServer.getChatRoomName(gameId)) : null;
            if (room != null) {
                room.sendMessage(SYSTEM, muted
                        ? game.getSeatId() + " muted the chat and won't see spectators' messages."
                        : game.getSeatId() + " unmuted the chat.", true);
            }
        }
        json(responseWriter, "{\"gameId\":" + quote(gameId) + ",\"chatMuted\":" + game.isChatMuted() + "}", baseHeaders());
    }

    private void play(HttpRequest request, ResponseWriter responseWriter) {
        QueryStringDecoder query = new QueryStringDecoder(request.uri());
        PlayNowSide side = PlayNowSide.fromCode(getQueryParameterSafely(query, "side"));
        PlayNowBot bot = PlayNowBot.fromChoice(getQueryParameterSafely(query, "bot"),
                getQueryParameterSafely(query, "difficulty"));
        StringBuilder location = new StringBuilder("/gemp-swccg/guest.html");
        String sep = "?";
        if (side != null) {
            location.append(sep).append("side=").append(side.getCode());
            sep = "&";
        }
        if (bot != null)
            location.append(sep).append("bot=").append(bot.getCode());
        Map<String, String> headers = baseHeaders();
        headers.put("Location", location.toString());
        responseWriter.writeError(302, headers);
    }

    /** The logged-in username, else the guest id from the cookie, else null. */
    private String currentOwner(HttpRequest request) {
        String loggedUser = getLoggedUser(request);
        return loggedUser != null ? loggedUser : getGuestId(request);
    }

    private static Map<String, String> baseHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Robots-Tag", NO_INDEX);
        headers.put(HttpHeaderNames.CACHE_CONTROL.toString(), "no-store");
        return headers;
    }

    private static void json(ResponseWriter responseWriter, String json, Map<String, String> headers) {
        headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=UTF-8");
        responseWriter.writeByteResponse(json.getBytes(StandardCharsets.UTF_8), headers);
    }

    private static void error(ResponseWriter responseWriter, int status, String code, String message, long retryAfterSeconds) {
        Map<String, String> headers = baseHeaders();
        headers.put(ERROR_HEADER, code);
        headers.put("message", message);
        if (retryAfterSeconds > 0)
            headers.put(HttpHeaderNames.RETRY_AFTER.toString(), String.valueOf(retryAfterSeconds));
        responseWriter.writeError(status, headers);
    }

    static String quote(String value) {
        if (value == null)
            return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            if (c == '"' || c == '\\')
                sb.append('\\').append(c);
            else if (c < 0x20)
                sb.append(String.format("\\u%04x", (int) c));
            else
                sb.append(c);
        }
        return sb.append('"').toString();
    }
}
