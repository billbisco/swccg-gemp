package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import com.gempukku.swccgo.service.AuthService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class RegisterRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    public RegisterRequestHandler(Map<Type, Object> context) {
        super(context);
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

                try {
                    if (!_gempSettingDAO.newAccountRegistrationEnabled()) {
                        throw new RegisterNotAllowedException();
                    }
                    if (_authService.register(login, password, remoteIp)) {
                        responseWriter.writeXmlResponse(null, logUserReturningHeaders(remoteIp, login));
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
        return "Password must be at least " + com.gempukku.swccgo.security.PasswordPolicy.MIN_LENGTH + " characters.";
    }
}
