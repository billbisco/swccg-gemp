package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.service.AuthService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class LoginRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    public LoginRequestHandler(Map<Type, Object> context) {
        super(context);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.POST) {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
            try {
                String login = getFormParameterSafely(postDecoder, "login");
                String password = getFormParameterSafely(postDecoder, "password");

                AuthService.AuthResult result = _authService.verifyCredentials(login, password, remoteIp);
                switch (result.getStatus()) {
                    case OK:
                        // Use the stored name, not what was typed, so "bill" logs in as "Bill".
                        responseWriter.writeXmlResponse(null, logUserReturningHeaders(remoteIp, result.getCanonicalName()));
                        return;
                    case RESET_REQUIRED:
                        // The password was a valid one-time reset code. Hand the browser a short-lived
                        // HttpOnly ticket; the existing "choose a new password" form posts to /register,
                        // which only accepts the reset when this ticket is present.
                        String ticket = _authService.createResetTicket(result.getPlayer());
                        responseWriter.writeError(202, Collections.singletonMap(HttpHeaderNames.SET_COOKIE.toString(),
                                encodeProtectedCookie(AuthService.RESET_COOKIE, ticket, 15 * 60, CookieHeaderNames.SameSite.Strict)));
                        return;
                    case BANNED:
                        throw new HttpProcessingException(403);
                    case TEMP_BANNED:
                        throw new HttpProcessingException(409);
                    case THROTTLED:
                        throw new HttpProcessingException(429);
                    case DEACTIVATED:
                    case INVALID:
                    default:
                        throw new HttpProcessingException(401);
                }
            }
            finally {
                postDecoder.destroy();
            }
        }
        else {
            throw new HttpProcessingException(404);
        }
    }

}
