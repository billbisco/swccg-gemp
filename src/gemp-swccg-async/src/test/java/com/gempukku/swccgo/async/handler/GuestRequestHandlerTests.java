package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.guest.GuestSettings;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.hall.HallServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GuestRequestHandlerTests {

    @Test
    public void PlayNowStartIsRefusedWhileGuestPlayIsOff() throws Exception {
        //test1: guest.enabled=false (the default) means 503 "disabled" and no game, for guests and members alike.
        GuestHandlerFixture f = new GuestHandlerFixture(GuestSettings.defaults());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);

        RecordingResponseWriter rw = f.guestStart(handler, null, "1.2.3.4");
        assertEquals(503, rw.status);
        assertEquals("disabled", rw.headers.get(GuestRequestHandler.ERROR_HEADER));
        assertNotNull(rw.headers.get("message"));

        RecordingResponseWriter member = f.guestStart(handler, f.member("luke"), "1.2.3.4");
        assertEquals(503, member.status);
        verify(f.hall, never()).startPlayNowGame(anyString(), nullable(Player.class), any(), any(), nullable(String.class));
    }

    @Test
    public void GuestStartGivesAGameAndAGuestCookie() throws Exception {
        //test2: a new guest gets a game, their seat, and a 15-day HttpOnly guestSession cookie; the response is noindex.
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);

        RecordingResponseWriter rw = f.guestStart(handler, null, "1.2.3.4");
        assertEquals(200, rw.status);
        assertTrue(rw.body.contains("\"gameId\":\"g1\""), rw.body);
        assertTrue(rw.body.contains("\"participantId\":\"Light Side\""), rw.body);
        assertTrue(rw.body.contains("\"guest\":true"), rw.body);
        assertEquals("noindex, nofollow", rw.headers.get("X-Robots-Tag"));

        String setCookie = rw.headers.get("set-cookie");
        assertNotNull(setCookie);
        assertTrue(setCookie.startsWith(GuestIdentity.COOKIE + "="), setCookie);
        assertTrue(setCookie.contains("HTTPOnly") || setCookie.contains("HttpOnly"), setCookie);
        assertTrue(setCookie.contains("Max-Age=1296000"), setCookie);
        assertTrue(setCookie.contains("SameSite=Lax"), setCookie);

        String token = Requests.cookieValue(setCookie);
        assertEquals(GuestIdentity.guestIdFor(token), f.games.get("g1").getOwner());
        verify(f.hall).startPlayNowGame(eq(GuestIdentity.guestIdFor(token)), nullable(Player.class),
                eq(PlayNowSide.LIGHT), eq(PlayNowBot.OZZEL), eq("1.2.3.4"));
    }

    @Test
    public void AReturningGuestKeepsTheirIdentity() throws Exception {
        //test3: a guest who already has a cookie keeps it; a junk cookie value is replaced with a fresh token.
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);
        String token = GuestIdentity.newToken();

        RecordingResponseWriter rw = f.guestStart(handler, GuestIdentity.COOKIE + "=" + token, "1.2.3.4");
        assertEquals(200, rw.status);
        assertEquals(token, Requests.cookieValue(rw.headers.get("set-cookie")));
        assertEquals(GuestIdentity.guestIdFor(token), f.games.get("g1").getOwner());

        RecordingResponseWriter junk = f.guestStart(handler, GuestIdentity.COOKIE + "=nope", "5.6.7.8");
        assertEquals(200, junk.status);
        assertTrue(GuestIdentity.isToken(Requests.cookieValue(junk.headers.get("set-cookie"))));
    }

    @Test
    public void AMemberPlaysUnderTheirName() throws Exception {
        //test4: a logged-in player's game is owned by their username, with their account, and no guest cookie is set.
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);

        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/start", Requests.post("/x", "side=dark&bot=yoda", f.member("luke")), f.context, rw, "1.2.3.4");
        assertEquals(200, rw.status);
        assertTrue(rw.body.contains("\"participantId\":\"luke\""), rw.body);
        assertTrue(rw.body.contains("\"guest\":false"), rw.body);
        assertNull(rw.headers.get("set-cookie"));
        PlayNowGames.Entry game = f.games.get("g1");
        assertEquals("luke", game.getOwner());
        assertEquals(PlayNowSide.DARK, game.getSide());
        assertEquals(PlayNowBot.YODA, game.getBot());
    }

    @Test
    public void BadSideOrBotIsABadRequest() throws Exception {
        //test5
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);
        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/start", Requests.post("/x", "side=purple&bot=ozzel", null), f.context, rw, "1.2.3.4");
        assertEquals(400, rw.status);
        assertEquals("bad_request", rw.headers.get(GuestRequestHandler.ERROR_HEADER));
    }

    @Test
    public void GuestLimitsComeBackAs429() throws Exception {
        //test6: two live guest games from one IP is the limit; the third start is 429 too_many_games.
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);
        assertEquals(200, f.guestStart(handler, null, "1.2.3.4").status);
        assertEquals(200, f.guestStart(handler, null, "1.2.3.4").status);

        RecordingResponseWriter third = f.guestStart(handler, null, "1.2.3.4");
        assertEquals(429, third.status);
        assertEquals("too_many_games", third.headers.get(GuestRequestHandler.ERROR_HEADER));

        //test7: a different IP is not affected.
        assertEquals(200, f.guestStart(handler, null, "9.9.9.9").status);
    }

    @Test
    public void HourlyStartLimitSendsRetryAfter() throws Exception {
        //test8: one start per IP per hour allowed; the second is 429 rate_limited with Retry-After.
        GuestSettings d = GuestSettings.defaults();
        GuestSettings settings = new GuestSettings(true, 20, 5, 1, 100, d.ipAllowlist(), 15, 30, 15);
        GuestHandlerFixture f = new GuestHandlerFixture(settings);
        GuestRequestHandler handler = new GuestRequestHandler(f.context);
        assertEquals(200, f.guestStart(handler, null, "1.2.3.4").status);
        RecordingResponseWriter second = f.guestStart(handler, null, "1.2.3.4");
        assertEquals(429, second.status);
        assertEquals("rate_limited", second.headers.get(GuestRequestHandler.ERROR_HEADER));
        assertTrue(Long.parseLong(second.headers.get("retry-after")) > 0);
    }

    @Test
    public void FullPlayNowTablesAreBusy() throws Exception {
        //test9: the hall's cap comes back as 503 busy with the hall's message.
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        when(f.hall.startPlayNowGame(anyString(), nullable(Player.class), any(), any(), nullable(String.class)))
                .thenThrow(new HallServer.PlayNowBusyException());
        RecordingResponseWriter rw = f.guestStart(new GuestRequestHandler(f.context), null, "1.2.3.4");
        assertEquals(503, rw.status);
        assertEquals("busy", rw.headers.get(GuestRequestHandler.ERROR_HEADER));
        assertTrue(rw.headers.get("message").contains("in use"));
    }

    @Test
    public void StatusListsOnlyTheCallersGames() throws Exception {
        //test10
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);
        String cookie = GuestIdentity.COOKIE + "=" + Requests.cookieValue(f.guestStart(handler, null, "1.2.3.4").headers.get("set-cookie"));
        f.guestStart(handler, null, "5.6.7.8");

        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/status", Requests.get("/x", cookie), f.context, rw, "1.2.3.4");
        assertEquals(200, rw.status);
        assertTrue(rw.body.contains("\"enabled\":true"), rw.body);
        assertTrue(rw.body.contains("\"loggedIn\":false"), rw.body);
        assertTrue(rw.body.contains("\"gameId\":\"g1\""), rw.body);
        assertFalse(rw.body.contains("\"g2\""), rw.body);

        RecordingResponseWriter none = new RecordingResponseWriter();
        handler.handleRequest("/status", Requests.get("/x", null), f.context, none, "1.2.3.4");
        assertTrue(none.body.contains("\"games\":[]"), none.body);
    }

    @Test
    public void PlayLinkRedirectsToTheGuestPage() throws Exception {
        //test11: known values are passed on; unknown ones are dropped.
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler handler = new GuestRequestHandler(f.context);
        RecordingResponseWriter rw = new RecordingResponseWriter();
        handler.handleRequest("/play", Requests.get("/gemp-swccg-server/guest/play?side=dark&bot=yoda", null), f.context, rw, "1.2.3.4");
        assertEquals(302, rw.status);
        assertEquals("/gemp-swccg/guest.html?side=dark&bot=yoda", rw.headers.get("Location"));

        RecordingResponseWriter junk = new RecordingResponseWriter();
        handler.handleRequest("/play", Requests.get("/x?side=%3Cscript%3E", null), f.context, junk, "1.2.3.4");
        assertEquals("/gemp-swccg/guest.html", junk.headers.get("Location"));
    }
}
