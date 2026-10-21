package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.save.GameSaveHeader;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.guest.GuestSeatPlayer;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.hall.HallServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Resume list and button, and a guest's games after they sign up. */
public class GuestResumeTests {
    private GuestHandlerFixture f;
    private GuestRequestHandler handler;
    private String token;
    private String guestId;
    private String cookie;

    @BeforeEach
    public void setUp() throws Exception {
        f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        handler = new GuestRequestHandler(f.context);
        token = GuestIdentity.newToken();
        guestId = GuestIdentity.guestIdFor(token);
        cookie = GuestIdentity.COOKIE + "=" + token;
    }

    private static GameSaveHeader savedHeader(String gameId, String seat, PlayNowSide side, PlayNowBot bot, int turn) {
        GameSaveHeader header = new GameSaveHeader();
        header.gameId = gameId;
        header.turn = turn;
        header.seats.add(GameSaveHeader.seat(seat, seat, null, GuestDecks.deck(side)));
        header.seats.add(GameSaveHeader.seat(bot.getPlayerId(), bot.getPlayerId(), bot.getCode(), GuestDecks.deck(side.opposite())));
        return header;
    }

    /** The hall puts a saved game back in play as the real one does: registered under its old id. */
    private void resumable(String owner, String gameId, String seat) throws Exception {
        when(f.hall.resumePlayNowGame(eq(owner), eq(gameId), anyString())).thenAnswer(inv -> {
            f.games.add(new PlayNowGames.Entry(gameId, owner, seat, PlayNowSide.DARK, PlayNowBot.YODA, inv.getArgument(2), false));
            when(f.swccgoServer.getGameById(gameId)).thenReturn(mock(SwccgGameMediator.class));
            return gameId;
        });
    }

    private RecordingResponseWriter post(String path, String form, String withCookie) throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest(path, Requests.post("/x", form, withCookie), f.context, rw, "1.2.3.4");
        return rw;
    }

    @Test
    public void StatusListsSavedGamesWithTheirTurn() throws Exception {
        //test1: a saved game shows its side, bot and turn so the page can offer Resume
        when(f.hall.getSavedPlayNowGames(guestId)).thenReturn(List.of(savedHeader("s1", "Dark Side", PlayNowSide.DARK, PlayNowBot.YODA, 7)));
        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/status", Requests.get("/x", cookie), f.context, rw, "1.2.3.4");
        assertTrue(rw.body.contains("{\"gameId\":\"s1\",\"participantId\":\"Dark Side\",\"side\":\"dark\",\"bot\":\"yoda\",\"saved\":true,\"turn\":7"), rw.body);
    }

    @Test
    public void ResumeOpensTheGameForItsOwner() throws Exception {
        //test1: the owner gets the game id and their seat back, and the guest cookie is renewed
        resumable(guestId, "s1", "Dark Side");
        RecordingResponseWriter rw = post("/resume", "gameId=s1", cookie);
        assertEquals(200, rw.status);
        assertTrue(rw.body.contains("\"gameId\":\"s1\""), rw.body);
        assertTrue(rw.body.contains("\"participantId\":\"Dark Side\""), rw.body);
        assertTrue(rw.body.contains("\"guest\":true"), rw.body);
        assertEquals(token, Requests.cookieValue(rw.headers.get("set-cookie")));

        //test2: someone else gets 404 (the hall finds no such game for them)
        RecordingResponseWriter other = post("/resume", "gameId=s1", GuestIdentity.COOKIE + "=" + GuestIdentity.newToken());
        assertEquals(404, other.status);
        //test3: no cookie and no login is 404 too, without asking the hall
        assertEquals(404, post("/resume", "gameId=s1", null).status);
    }

    @Test
    public void ResumeWhenFullIsBusy() throws Exception {
        //test1: the busy message from the hall reaches the player
        when(f.hall.resumePlayNowGame(anyString(), anyString(), anyString())).thenThrow(new HallServer.PlayNowBusyException());
        RecordingResponseWriter rw = post("/resume", "gameId=s1", cookie);
        assertEquals(503, rw.status);
        assertEquals("busy", rw.headers.get(GuestRequestHandler.ERROR_HEADER));
        assertTrue(rw.headers.get("message").contains("in use"));
    }

    @Test
    public void ASaveFromAnOlderVersionIsExplained() throws Exception {
        //test1
        when(f.hall.resumePlayNowGame(anyString(), anyString(), anyString())).thenThrow(new HallServer.PlayNowIncompatibleException());
        RecordingResponseWriter rw = post("/resume", "gameId=s1", cookie);
        assertEquals(409, rw.status);
        assertEquals("incompatible", rw.headers.get(GuestRequestHandler.ERROR_HEADER));
        assertTrue(rw.headers.get("message").contains("update"));
    }

    @Test
    public void AGuestAtTheirLimitCantBringBackAnotherGame() throws Exception {
        //test1: two live guest games from this IP already; resuming a third is 429 and the hall isn't asked
        f.guestStart(handler, cookie, "1.2.3.4");
        f.guestStart(handler, cookie, "1.2.3.4");
        RecordingResponseWriter rw = post("/resume", "gameId=s1", cookie);
        assertEquals(429, rw.status);
        verify(f.hall, never()).resumePlayNowGame(anyString(), eq("s1"), anyString());
    }

    @Test
    public void AMemberResumesUnderTheirAccount() throws Exception {
        //test1: members resume by login, with no guest cookie
        String member = f.member("luke");
        resumable("luke", "s2", "luke");
        RecordingResponseWriter rw = post("/resume", "gameId=s2", member);
        assertEquals(200, rw.status);
        assertTrue(rw.body.contains("\"guest\":false"), rw.body);
        assertFalse(rw.headers.containsKey("set-cookie"));
    }

    @Test
    public void SigningUpMovesTheGuestsGamesToTheNewAccount() throws Exception {
        //test1: registering with a guest cookie hands the guest's games to the hall for the new account
        com.gempukku.swccgo.service.AuthService auth = mock(com.gempukku.swccgo.service.AuthService.class);
        when(auth.register(eq("luke"), anyString(), anyString())).thenReturn(true);
        com.gempukku.swccgo.db.GempSettingDAO settings = mock(com.gempukku.swccgo.db.GempSettingDAO.class);
        when(settings.newAccountRegistrationEnabled()).thenReturn(true);
        f.context.put(com.gempukku.swccgo.service.AuthService.class, auth);
        f.context.put(com.gempukku.swccgo.db.GempSettingDAO.class, settings);
        RegisterRequestHandler register = new RegisterRequestHandler(f.context);

        RecordingResponseWriter rw = new RecordingResponseWriter();
        register.handleRequest("", Requests.post("/x", "login=luke&password=a-long-password", cookie), f.context, rw, "1.2.3.4");
        assertEquals(200, rw.status);
        verify(f.hall).adoptGuestGames(guestId, "luke");
    }

    @Test
    public void SigningUpWithoutAGuestCookieAdoptsNothing() throws Exception {
        //test1
        com.gempukku.swccgo.service.AuthService auth = mock(com.gempukku.swccgo.service.AuthService.class);
        when(auth.register(anyString(), anyString(), anyString())).thenReturn(true);
        com.gempukku.swccgo.db.GempSettingDAO settings = mock(com.gempukku.swccgo.db.GempSettingDAO.class);
        when(settings.newAccountRegistrationEnabled()).thenReturn(true);
        f.context.put(com.gempukku.swccgo.service.AuthService.class, auth);
        f.context.put(com.gempukku.swccgo.db.GempSettingDAO.class, settings);
        new RegisterRequestHandler(f.context).handleRequest("", Requests.post("/x", "login=luke&password=a-long-password", null),
                f.context, new RecordingResponseWriter(), "1.2.3.4");
        verify(f.hall, never()).adoptGuestGames(anyString(), anyString());
    }

    @Test
    public void AfterSignupThePlayerCarriesOnAsTheirSeat() throws Exception {
        //test1: once the game is theirs, the logged-in player acts as "Light Side" in it (seat ids can't change)
        f.guestStart(handler, cookie, "1.2.3.4");
        f.games.adopt(guestId, "luke");
        String member = f.member("luke");
        SwccgGameMediator mediator = f.swccgoServer.getGameById("g1");
        when(mediator.produceCardInfo(any(Player.class), anyInt())).thenAnswer(inv -> {
            Player p = inv.getArgument(0);
            return p.getName() + (p instanceof GuestSeatPlayer ? " for " + ((GuestSeatPlayer) p).getAccount() : "");
        });
        GameRequestHandler game = new GameRequestHandler(f.context, null);
        RecordingResponseWriter rw = new RecordingResponseWriter();
        game.handleRequest("/g1/cardInfo", Requests.get("/x?cardId=1&participantId=Light%20Side", member), f.context, rw, "1.2.3.4");
        assertEquals("Light Side for luke", rw.body);

        //test2: they are not a guest any more: they may chat in their game, as themselves
        assertFalse(GuestSeatPlayer.isGuest(new GuestSeatPlayer("Light Side", "luke")));
        ChatRequestHandler chat = new ChatRequestHandler(f.context, null);
        RecordingResponseWriter posted = new RecordingResponseWriter();
        chat.handleRequest("/Gameg1", Requests.post("/x", "participantId=x&message=thanks", member), f.context, posted, "1.2.3.4");
        assertEquals(200, posted.status);

        //test3: the old guest cookie no longer opens the game
        try {
            game.handleRequest("/g1/cardInfo", Requests.get("/x?cardId=1", cookie), f.context, new RecordingResponseWriter(), "1.2.3.4");
            throw new AssertionError("expected 401");
        } catch (HttpProcessingException exp) {
            assertEquals(401, exp.getStatus());
        }
    }
}
