package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.service.AuthService;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Logging in to an existing account with a guest cookie moves the guest's Play now games to it. */
public class GuestLoginAdoptionTests {
    private GuestHandlerFixture f;
    private AuthService auth;
    private LoginRequestHandler loginHandler;
    private String guestId;
    private String cookie;

    @BeforeEach
    public void setUp() throws Exception {
        f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        auth = mock(AuthService.class);
        Player luke = new Player(7, "Luke", "x", "u", null, null, null, null);
        when(auth.verifyCredentials(eq("luke"), eq("right-password"), anyString())).thenReturn(authResult(AuthService.Status.OK, luke));
        when(auth.verifyCredentials(eq("luke"), eq("wrong-password"), anyString())).thenReturn(authResult(AuthService.Status.INVALID, null));
        f.context.put(AuthService.class, auth);
        loginHandler = new LoginRequestHandler(f.context);
        String token = GuestIdentity.newToken();
        guestId = GuestIdentity.guestIdFor(token);
        cookie = GuestIdentity.COOKIE + "=" + token;
    }

    // AuthResult is final with a package-private constructor; build real ones.
    private static AuthService.AuthResult authResult(AuthService.Status status, Player player) throws Exception {
        Constructor<AuthService.AuthResult> c = AuthService.AuthResult.class.getDeclaredConstructor(AuthService.Status.class, Player.class);
        c.setAccessible(true);
        return c.newInstance(status, player);
    }

    private RecordingResponseWriter logIn(String password, String cookieHeader) throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        loginHandler.handleRequest("", Requests.post("/x", "login=luke&password=" + password, cookieHeader), f.context, rw, "1.2.3.4");
        return rw;
    }

    @Test
    public void LoggingInMovesTheGuestsGamesToTheAccount() throws Exception {
        RecordingResponseWriter rw = logIn("right-password", cookie);

        //test1: the guest's games go to the account under its stored name, not what was typed
        verify(f.hall).adoptGuestGames(guestId, "Luke");
        //test2: a normal login: logged in, no notice
        assertEquals(200, rw.status);
        assertTrue(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()).startsWith("loggedUser="));
        assertFalse(rw.headers.containsKey(SwccgoServerRequestHandler.GUEST_NOTICE_HEADER));
    }

    @Test
    public void LoginStillSucceedsWhenTheGuestGameCantBeMoved() throws Exception {
        doThrow(new RuntimeException("Unable to move saved games: connection refused"))
                .when(f.hall).adoptGuestGames(guestId, "Luke");

        RecordingResponseWriter rw = logIn("right-password", cookie);

        //test1: still logged in
        assertEquals(200, rw.status);
        assertTrue(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()).startsWith("loggedUser="));
        //test2: with a plain notice that the game wasn't moved
        assertEquals(LoginRequestHandler.LOGIN_ADOPT_FAILED_NOTICE, rw.headers.get(SwccgoServerRequestHandler.GUEST_NOTICE_HEADER));
    }

    @Test
    public void LoggingInWithoutAGuestCookieAdoptsNothing() throws Exception {
        //test1
        RecordingResponseWriter rw = logIn("right-password", null);
        assertEquals(200, rw.status);
        verify(f.hall, never()).adoptGuestGames(anyString(), anyString());
    }

    @Test
    public void AFailedLoginAdoptsNothing() throws Exception {
        //test1: a wrong password is still 401, and the guest's games stay the guest's
        HttpProcessingException refused = assertThrows(HttpProcessingException.class, () -> logIn("wrong-password", cookie));
        assertEquals(401, refused.getStatus());
        verify(f.hall, never()).adoptGuestGames(anyString(), anyString());
    }
}
