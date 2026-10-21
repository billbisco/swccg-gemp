package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.guest.GuestSettings;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.service.LoggedUserHolder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The context the guest handlers need. The hall is mocked: starting a game just registers it, so these tests cover
 * the HTTP layer (the real hall is covered by PlayNowGameTests in the server module).
 */
class GuestHandlerFixture {
    final PlayNowGames games = new PlayNowGames();
    final LoggedUserHolder loggedUsers = new LoggedUserHolder();
    final PlayerDAO playerDao = mock(PlayerDAO.class);
    final HallServer hall = mock(HallServer.class);
    final SwccgoServer swccgoServer = mock(SwccgoServer.class);
    final ChatServer chatServer = new ChatServer();
    final Map<Type, Object> context = new HashMap<>();
    private final AtomicInteger _nextGame = new AtomicInteger(1);

    GuestHandlerFixture(GuestSettings settings) throws Exception {
        context.put(PlayNowGames.class, games);
        context.put(LoggedUserHolder.class, loggedUsers);
        context.put(PlayerDAO.class, playerDao);
        context.put(HallServer.class, hall);
        context.put(SwccgoServer.class, swccgoServer);
        context.put(ChatServer.class, chatServer);
        context.put(GuestSettings.class, settings);
        when(hall.startPlayNowGame(anyString(), nullable(Player.class), any(PlayNowSide.class), any(PlayNowBot.class), nullable(String.class)))
                .thenAnswer(inv -> {
                    String gameId = "g" + _nextGame.getAndIncrement();
                    Player account = inv.getArgument(1);
                    PlayNowSide side = inv.getArgument(2);
                    PlayNowBot bot = inv.getArgument(3);
                    String seat = account != null ? account.getName() : side.getSeatName();
                    games.add(new PlayNowGames.Entry(gameId, inv.getArgument(0), seat, side, bot, inv.getArgument(4), false));
                    SwccgGameMediator mediator = mock(SwccgGameMediator.class);
                    when(swccgoServer.getGameById(gameId)).thenReturn(mediator);
                    chatServer.createChatRoom(SwccgoServer.getChatRoomName(gameId), false, 30, Set.of(seat, bot.getPlayerId()), true, false);
                    return gameId;
                });
    }

    static GuestSettings enabled() {
        GuestSettings d = GuestSettings.defaults();
        return new GuestSettings(true, d.maxConcurrentGames(), d.maxConcurrentPerIp(), d.startsPerIpPerHour(),
                d.startsPerHourGlobal(), d.ipAllowlist(), d.unloadIdleMinutes(), d.capEvictIdleMinutes(), d.saveRetentionDays());
    }

    /** Logs a member in and returns the cookie header to send. */
    String member(String name) {
        Player player = new Player(7, name, "x", "u", null, null, null, null);
        when(playerDao.getPlayer(name)).thenReturn(player);
        return "loggedUser=" + loggedUsers.logUser(name);
    }

    /** Starts a guest game over HTTP and returns the response. */
    RecordingResponseWriter guestStart(GuestRequestHandler handler, String cookie, String ip) throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/start", Requests.post("/gemp-swccg-server/guest/start", "side=light&bot=ozzel", cookie), context, rw, ip);
        return rw;
    }
}
