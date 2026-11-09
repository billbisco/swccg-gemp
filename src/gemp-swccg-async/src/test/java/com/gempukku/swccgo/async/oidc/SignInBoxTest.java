package com.gempukku.swccgo.async.oidc;

import com.gempukku.swccgo.async.handler.OidcRequestHandlerAccess;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The in-page sign-in box: its OIDC request and how failures come back to the login page. */
public class SignInBoxTest {
    private static OidcClient client() {
        return new OidcClient(new OidcConfig("https://login.example", "gemp-play", "0123456789abcdef0123456789abcdef",
                "https://play.example/gemp-swccg-server/oidc/callback", "/hall.html"));
    }

    @Test
    public void boxParamsAreAFullPkceRequest() throws Exception {
        OidcClient c = client();
        String verifier = c.random();
        JSONObject o = c.boxParams("st", "no", verifier);
        assertEquals("https://login.example", o.get("issuer"));
        assertEquals("gemp-play", o.get("client_id"));
        assertEquals("https://play.example/gemp-swccg-server/oidc/callback", o.get("redirect_uri"));
        assertEquals("code", o.get("response_type"));
        assertEquals("openid profile", o.get("scope"));
        assertEquals("st", o.get("state"));
        assertEquals("no", o.get("nonce"));
        assertEquals("S256", o.get("code_challenge_method"));
        assertEquals(OidcClient.challenge(verifier), o.get("code_challenge"));
        assertFalse(o.toJSONString().contains(verifier), "the PKCE verifier never leaves the server");
        assertFalse(o.toJSONString().contains("0123456789abcdef"), "nor the client secret");
    }

    @Test
    public void onlyKnownReasonsReachThePage() {
        assertEquals("bad_credentials", OidcRequestHandlerAccess.reason("access_denied", "bad_credentials"));
        assertEquals("name_taken", OidcRequestHandlerAccess.reason("access_denied", "name_taken"));
        assertEquals("cancelled", OidcRequestHandlerAccess.reason("access_denied", "<script>"));
        assertEquals("cancelled", OidcRequestHandlerAccess.reason("access_denied", null));
        assertEquals("failed", OidcRequestHandlerAccess.reason("server_error", "bad_credentials&x=1"));
        assertEquals("failed", OidcRequestHandlerAccess.reason("invalid_request", null));
    }
}
