package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import com.gempukku.swccgo.service.AuthService;
import com.gempukku.swccgo.security.SignupGuard;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class RegisterRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    static final String SIGNUP_ADOPT_FAILED_NOTICE = "Your account was created, but we couldn't move your Play now game to it. "
            + "The game is still saved for you as a guest: log out and open Play now in this browser to carry on.";

    static final String NAME_RESERVED = "That name is reserved. Choose another.";
    static final String NAME_HELD = "That name is taken or held for someone else. Choose another.";
    static final String NAME_UNCHECKED = "We couldn't check that name just now. Try again in a few minutes.";

    private final HallServer _hallServer;
    /** Asks the sign-in service about holds, reserved and issued names; null when it is not set up. */
    private final com.gempukku.swccgo.async.oidc.SiteNameCheck _nameCheck;

    public RegisterRequestHandler(Map<Type, Object> context) {
        this(context, com.gempukku.swccgo.async.oidc.SiteNameCheck.fromEnvironment());
    }

    RegisterRequestHandler(Map<Type, Object> context, com.gempukku.swccgo.async.oidc.SiteNameCheck nameCheck) {
        super(context);
        _hallServer = extractObject(context, HallServer.class);
        _nameCheck = nameCheck;
    }

    /**
     * Why classic signup may not take this name, or null (swccg-guild/work#261): Play's guest seat names, and,
     * when the sign-in service is set up, its reserved names, the 90-day hold after a rename or close, and names it
     * has issued. If the sign-in service can't answer, the signup is refused (fail closed).
     */
    static String nameRefusal(String login, com.gempukku.swccgo.async.oidc.SiteNameCheck nameCheck) {
        if (login == null)
            return null;
        if (com.gempukku.swccgo.guest.PlayNowSide.isSeatName(login))
            return NAME_RESERVED;
        if (nameCheck == null)
            return null;
        switch (nameCheck.check(login)) {
            case AVAILABLE: return null;
            case RESERVED: return NAME_RESERVED;
            case HELD:
            case TAKEN: return NAME_HELD;
            default: return NAME_UNCHECKED;
        }
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.POST) {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
            try {
                String login = getFormParameterSafely(postDecoder, "login");
                String password = getFormParameterSafely(postDecoder, "password");

                // Finishing an admin reset: only with the ticket issued when the player logged in
                // with their one-time code. Without it, a reset account is just a taken name.
                String resetTicket = getCookieValue(request, AuthService.RESET_COOKIE);
                if (resetTicket != null && _authService.hasValidResetTicket(resetTicket, login)) {
                    AuthService.ResetOutcome outcome = _authService.completeReset(resetTicket, login, password);
                    if (outcome == AuthService.ResetOutcome.WEAK_PASSWORD)
                        throw new HttpProcessingException(400, weakPasswordMessage());
                    if (outcome != AuthService.ResetOutcome.OK)
                        throw new HttpProcessingException(400);
                    // 202 = "password reset, please log in" in the existing client.
                    responseWriter.writeError(202, Collections.singletonMap(HttpHeaderNames.SET_COOKIE.toString(),
                            encodeProtectedCookie(AuthService.RESET_COOKIE, "", 0, CookieHeaderNames.SameSite.Strict)));
                    return;
                }

                // Honeypot: a hidden "hp_field" field humans never fill (named so browser autofill leaves it alone). Pretend it worked, create nothing.
                if (SignupGuard.isHoneypotTripped(getFormParameterSafely(postDecoder, SignupGuard.HONEYPOT_FIELD))) {
                    responseWriter.writeXmlResponse(null);
                    return;
                }

                try {
                    if (!_gempSettingDAO.newAccountRegistrationEnabled()) {
                        throw new RegisterNotAllowedException();
                    }
                    String refusal = nameRefusal(login, _nameCheck);
                    if (refusal != null)
                        throw new HttpProcessingException(400, refusal);
                    if (_authService.register(login, password, remoteIp)) {
                        // A guest who signs up keeps their Play now games; they count on the new account.
                        // The account exists now, so a failed move never fails the signup.
                        responseWriter.writeXmlResponse(null, adoptGuestGamesSafely(_hallServer, request, login,
                                logUserReturningHeaders(remoteIp, login), SIGNUP_ADOPT_FAILED_NOTICE));
                    } else {
                        throw new HttpProcessingException(403);
                    }
                } catch (AuthService.WeakPasswordException exp) {
                    throw new HttpProcessingException(400, weakPasswordMessage());
                } catch (LoginInvalidException exp) {
                    throw new HttpProcessingException(400);
                } catch (RegisterNotAllowedException exp) {
                    throw new HttpProcessingException(405);
                }
            }
            finally {
                postDecoder.destroy();
            }
        } else {
            responseWriter.writeError(404);
        }
    }

    private static String weakPasswordMessage() {
        return com.gempukku.swccgo.security.PasswordPolicy.rejectionMessage();
    }
}
