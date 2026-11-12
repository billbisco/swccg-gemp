package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.oidc.OidcClient;
import com.gempukku.swccgo.async.oidc.OidcConfig;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.guest.GuestIdentity;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * A membership made through the sign-in service carries this browser's guest Play now games over on its first Play
 * session, like a classic signup (swccg-guild/work#264 R6/AC3; Bill 2026-11-11, t135u). Existing accounts signing
 * in never take them.
 */
public class SsoSignupGuestCarryOverTests {
    private GuestHandlerFixture f;
    private OidcRequestHandler oidc;
    private String guestId;
    private String guestCookie;

    @BeforeEach
    public void setUp() throws Exception {
        f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        when(f.playerDao.getPlayer("Rey Sky")).thenReturn(new Player(3812, "Rey Sky", "x", "u", null, null, null, null));
        when(f.playerDao.updateLastLoginIp(anyString(), anyString())).thenReturn(true);
        oidc = new OidcRequestHandler(f.context, new OidcClient(new OidcConfig("https://login.example", "gemp-play",
                "0123456789abcdef0123456789abcdef", "https://play.example/gemp-swccg-server/oidc/callback", "/gemp-swccg/hall.html")));
        String token = GuestIdentity.newToken();
        guestId = GuestIdentity.guestIdFor(token);
        guestCookie = GuestIdentity.COOKIE + "=" + token;
    }

    private RecordingResponseWriter signIn(String cookie) throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        oidc.completeSignIn(Requests.get("/gemp-swccg-server/oidc/callback", cookie), rw, "1.2.3.4", "Rey Sky", 3812);
        return rw;
    }

    @Test
    public void ANewMembershipTakesThisBrowsersGuestGames() throws Exception {
        when(f.playerDao.claimFirstPlaySession("Rey Sky", "1.2.3.4", OidcRequestHandler.FIRST_SESSION_MAX_AGE_SECONDS)).thenReturn(true);

        RecordingResponseWriter rw = signIn(guestCookie);

        //test1: the guest's games go to the new account, through the same path as classic signup
        verify(f.hall).adoptGuestGames(guestId, "Rey Sky");
        //test2: signed in and back to the hall, with no notice
        assertEquals(302, rw.status);
        assertEquals("/gemp-swccg/hall.html", rw.headers.get("Location"));
        assertTrue(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()).startsWith("loggedUser="));
        assertFalse(rw.headers.containsKey(SwccgoServerRequestHandler.GUEST_NOTICE_HEADER));
    }

    @Test
    public void AnExistingAccountNeverTakesThem() throws Exception {
        when(f.playerDao.claimFirstPlaySession(anyString(), anyString(), anyInt())).thenReturn(false);

        RecordingResponseWriter rw = signIn(guestCookie);

        //test1: not the account's first Play session (or not a new account): nothing moves
        verify(f.hall, never()).adoptGuestGames(anyString(), anyString());
        assertEquals(302, rw.status);
        assertTrue(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()).startsWith("loggedUser="));
    }

    @Test
    public void NoGuestCookieNoClaim() throws Exception {
        RecordingResponseWriter rw = signIn(null);

        //test1: without a guest cookie the first session isn't even checked, and nothing moves
        verify(f.playerDao, never()).claimFirstPlaySession(anyString(), anyString(), anyInt());
        verify(f.hall, never()).adoptGuestGames(anyString(), anyString());
        assertEquals(302, rw.status);
    }

    @Test
    public void AFailedMoveStillSignsInWithTheNotice() throws Exception {
        when(f.playerDao.claimFirstPlaySession(anyString(), anyString(), anyInt())).thenReturn(true);
        doThrow(new RuntimeException("Unable to move saved games")).when(f.hall).adoptGuestGames(guestId, "Rey Sky");

        RecordingResponseWriter rw = signIn(guestCookie);

        //test1: signed in, and the hall is told to show the same notice as a classic signup
        assertEquals(302, rw.status);
        assertTrue(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()).startsWith("loggedUser="));
        assertEquals("/gemp-swccg/hall.html?" + OidcRequestHandler.ADOPT_FAILED_PARAM, rw.headers.get("Location"));
        assertFalse(rw.headers.containsKey(SwccgoServerRequestHandler.GUEST_NOTICE_HEADER));
    }

    @Test
    public void TheHallShowsTheSameNotice() throws Exception {
        //test1: hallUi.js shows RegisterRequestHandler.SIGNUP_ADOPT_FAILED_NOTICE word for word for the parameter
        String js = java.nio.file.Files.readString(java.nio.file.Paths.get("src", "main", "web", "js", "gemp-016", "hallUi.js"));
        String notice = RegisterRequestHandler.SIGNUP_ADOPT_FAILED_NOTICE;
        int cut = notice.indexOf("it. ") + 4;
        assertTrue(js.contains("\"" + notice.substring(0, cut) + "\"\n    + \"" + notice.substring(cut) + "\""));
        assertTrue(js.contains("guest_notice=adopt_failed") && OidcRequestHandler.ADOPT_FAILED_PARAM.equals("guest_notice=adopt_failed"));
        assertTrue(js.contains("this.showGuestCarryOverNotice();"));
    }
}
