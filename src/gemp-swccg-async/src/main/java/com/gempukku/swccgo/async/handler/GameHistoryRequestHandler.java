package com.gempukku.swccgo.async.handler;

import com.alibaba.fastjson.JSON;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.db.GameHistoryFilter;
import com.gempukku.swccgo.db.vo.GameHistoryEntry;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.game.Player;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameHistoryRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private GameHistoryService _gameHistoryService;

    public GameHistoryRequestHandler(Map<Type, Object> context) {
        super(context);

        _gameHistoryService = extractObject(context, GameHistoryService.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.GET) {
            getGameHistory(request, responseWriter);
        } else if ("/filters".equals(uri) && request.method() == HttpMethod.GET) {
            getFilterOptions(request, responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    /**
     * GET /gameHistory?start=&count=[&format=][&opponent=][&opponentExact=true][&event=][&side=Dark|Light]
     * [&result=W|L][&from=yyyy-MM-dd][&to=yyyy-MM-dd]
     * <p>
     * The player's games, newest first.  start defaults to 0 and count to 25 (at most 100).  The filters are optional:
     * format is an exact format name; opponent and event match the start of the name ignoring case (event=casual means
     * casual games); side is the side the player played; from/to are inclusive UTC days on which the game ended.
     * <p>
     * The root carries count (games matching the filters), total (all the player's games), start and pageSize.  Each
     * historyEntry keeps its original attributes and adds result (W/L), opponent, mySide, winnerSide, leagueType (for
     * league games) and deckName (the player's own deck, whether or not a replay was kept).
     */
    private void getGameHistory(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        int start = parseInt(getQueryParameterSafely(queryDecoder, "start"), 0);
        int count = parseInt(getQueryParameterSafely(queryDecoder, "count"), GameHistoryService.DEFAULT_HISTORY_PAGE_SIZE);

        if (start < 0 || count < 1 || count > GameHistoryService.MAX_HISTORY_PAGE_SIZE)
            throw new HttpProcessingException(400);

        GameHistoryFilter filter = new GameHistoryFilter(
                getQueryParameterSafely(queryDecoder, "format"),
                getQueryParameterSafely(queryDecoder, "opponent"),
                "true".equalsIgnoreCase(getQueryParameterSafely(queryDecoder, "opponentExact")),
                getQueryParameterSafely(queryDecoder, "event"),
                getQueryParameterSafely(queryDecoder, "side"),
                getQueryParameterSafely(queryDecoder, "result"),
                parseDay(getQueryParameterSafely(queryDecoder, "from")),
                parseDay(getQueryParameterSafely(queryDecoder, "to")));
        String problem = filter.validate();
        if (problem != null)
            throw new HttpProcessingException(400, problem);

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        String me = resourceOwner.getName();

        GameHistoryService.HistoryPage page = _gameHistoryService.getGameHistoryPage(resourceOwner, filter, start, count);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element gameHistory = doc.createElement("gameHistory");
        gameHistory.setAttribute("count", String.valueOf(page.matching()));
        gameHistory.setAttribute("total", String.valueOf(page.total()));
        gameHistory.setAttribute("start", String.valueOf(page.start()));
        gameHistory.setAttribute("pageSize", String.valueOf(page.count()));
        gameHistory.setAttribute("playerId", me);

        for (GameHistoryEntry gameHistoryEntry : page.entries()) {
            Element historyEntry = doc.createElement("historyEntry");
            historyEntry.setAttribute("winner", gameHistoryEntry.getWinner());
            historyEntry.setAttribute("winnerDeckArchetype", nullToEmpty(gameHistoryEntry.getwinnerDeckArchetype()));
            historyEntry.setAttribute("loser", gameHistoryEntry.getLoser());
            historyEntry.setAttribute("loserDeckArchetype", nullToEmpty(gameHistoryEntry.getloserDeckArchetype()));

            historyEntry.setAttribute("winReason", gameHistoryEntry.getWinReason());
            historyEntry.setAttribute("loseReason", gameHistoryEntry.getLoseReason());

            if (gameHistoryEntry.getFormatName() != null)
                historyEntry.setAttribute("formatName", gameHistoryEntry.getFormatName());
            String tournament = gameHistoryEntry.getTournament();
            if (tournament != null)
                historyEntry.setAttribute("tournament", tournament);
            String leagueType = gameHistoryEntry.getLeagueType();
            if (leagueType != null && !leagueType.isEmpty())
                historyEntry.setAttribute("leagueType", leagueType);

            boolean won = me.equals(gameHistoryEntry.getWinner());
            historyEntry.setAttribute("result", won ? GameHistoryFilter.WON : GameHistoryFilter.LOST);
            historyEntry.setAttribute("opponent", won ? gameHistoryEntry.getLoser() : gameHistoryEntry.getWinner());
            String mySide = gameHistoryEntry.getSideOf(me);
            if (mySide != null)
                historyEntry.setAttribute("mySide", mySide);
            if (gameHistoryEntry.getWinnerSide() != null)
                historyEntry.setAttribute("winnerSide", gameHistoryEntry.getWinnerSide());

            String deckName = won ? gameHistoryEntry.getWinnerDeckName() : gameHistoryEntry.getLoserDeckName();
            if (deckName != null)
                historyEntry.setAttribute("deckName", deckName);
            // the replay link stays as before: only the player's own recording of their own game
            String recordingId = won ? gameHistoryEntry.getWinnerRecording() : gameHistoryEntry.getLoserRecording();
            if (recordingId != null)
                historyEntry.setAttribute("gameRecordingId", recordingId);

            historyEntry.setAttribute("startTime", String.valueOf(gameHistoryEntry.getStartTime().getTime()));
            historyEntry.setAttribute("endTime", String.valueOf(gameHistoryEntry.getEndTime().getTime()));

            gameHistory.appendChild(historyEntry);
        }

        doc.appendChild(gameHistory);

        responseWriter.writeXmlResponse(doc);
    }

    /** GET /gameHistory/filters -> {"formats": [...most played first], "events": [...most recent first, max 100]} */
    private void getFilterOptions(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        Player resourceOwner = getResourceOwnerSafely(request, getQueryParameterSafely(queryDecoder, "participantId"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("formats", _gameHistoryService.getGameHistoryFormats(resourceOwner));
        result.put("events", _gameHistoryService.getGameHistoryEvents(resourceOwner));
        responseWriter.writeJsonResponse(JSON.toJSONString(result));
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    static int parseInt(String value, int fallback) throws HttpProcessingException {
        if (value == null || value.isBlank())
            return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exp) {
            throw new HttpProcessingException(400);
        }
    }

    static LocalDate parseDay(String value) throws HttpProcessingException {
        if (value == null || value.isBlank())
            return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exp) {
            throw new HttpProcessingException(400, "Dates must be given as yyyy-MM-dd.");
        }
    }
}
