package com.gempukku.swccgo.async.handler;

/** Test access to OidcRequestHandler's package-private helpers. */
public final class OidcRequestHandlerAccess {
    private OidcRequestHandlerAccess() {
    }

    public static String reason(String error, String description) {
        return OidcRequestHandler.ssoErrorReason(error, description);
    }
}
