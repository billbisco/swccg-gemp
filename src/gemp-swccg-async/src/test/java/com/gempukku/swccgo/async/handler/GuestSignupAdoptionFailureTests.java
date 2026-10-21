package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.service.AuthService;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Signing up with a guest cookie when the guest's games can't be moved to the new account. */
public class GuestSignupAdoptionFailureTests {
    private GuestHandlerFixture f;
    private RegisterRequestHandler register;
    private String guestId;
    private String cookie;

    @BeforeEach
    public void setUp() throws Exception {
        f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        AuthService auth = mock(AuthService.class);
        when(auth.register(eq("luke"), anyString(), anyString())).thenReturn(true);
        GempSettingDAO settings = mock(GempSettingDAO.class);
        when(settings.newAccountRegistrationEnabled()).thenReturn(true);
        f.context.put(AuthService.class, auth);
        f.context.put(GempSettingDAO.class, settings);
        register = new RegisterRequestHandler(f.context);
        String token = GuestIdentity.newToken();
        guestId = GuestIdentity.guestIdFor(token);
        cookie = GuestIdentity.COOKIE + "=" + token;
    }

    private RecordingResponseWriter signUp() throws Exception {
        RecordingResponseWriter rw = new RecordingResponseWriter();
        register.handleRequest("", Requests.post("/x", "login=luke&password=a-long-password", cookie), f.context, rw, "1.2.3.4");
        return rw;
    }

    @Test
    public void SignupStillSucceedsWhenTheGuestGameCantBeMoved() throws Exception {
        doThrow(new RuntimeException("Unable to move saved games: connection refused"))
                .when(f.hall).adoptGuestGames(guestId, "luke");

        RecordingResponseWriter rw = signUp();

        //test1: the move was tried for the new account
        verify(f.hall).adoptGuestGames(guestId, "luke");
        //test2: the signup is still a success, and the player is logged in to the new account
        assertEquals(200, rw.status);
        assertNotNull(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()));
        assertTrue(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()).startsWith("loggedUser="));
        //test3: with a plain notice that the account is fine but the game wasn't moved
        assertEquals(RegisterRequestHandler.SIGNUP_ADOPT_FAILED_NOTICE, rw.headers.get(SwccgoServerRequestHandler.GUEST_NOTICE_HEADER));
        assertTrue(RegisterRequestHandler.SIGNUP_ADOPT_FAILED_NOTICE.startsWith("Your account was created, but we couldn't move your Play now game to it."));
    }

    @Test
    public void ANormalSignupHasNoNotice() throws Exception {
        RecordingResponseWriter rw = signUp();

        //test1: the guest's games are handed to the new account, as before
        verify(f.hall).adoptGuestGames(guestId, "luke");
        //test2: success, logged in, and no notice
        assertEquals(200, rw.status);
        assertTrue(rw.headers.get(HttpHeaderNames.SET_COOKIE.toString()).startsWith("loggedUser="));
        assertFalse(rw.headers.containsKey(SwccgoServerRequestHandler.GUEST_NOTICE_HEADER));
    }
}
