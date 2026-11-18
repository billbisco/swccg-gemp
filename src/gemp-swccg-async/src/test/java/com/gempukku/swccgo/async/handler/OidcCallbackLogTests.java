package com.gempukku.swccgo.async.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** #630: Play's sign-in callback line carries the tracking id and the state fingerprint, and ids only. */
public class OidcCallbackLogTests {
    @Test
    public void goodSignInLineHasTrackingIdAndPlayer() {
        assertEquals("sso-callback result=ok tid=0123456789abcdef0123456789abcdef sfp=ba7816bf8f01cfea player=3841",
                OidcRequestHandler.callbackLine("ok", "0123456789abcdef0123456789abcdef", "abc", 3841));
    }

    @Test
    public void refusalLineHasStateFingerprintNotState() {
        String line = OidcRequestHandler.callbackLine("bad_state", null, "abc", null);
        assertEquals("sso-callback result=bad_state tid=- sfp=ba7816bf8f01cfea player=-", line);
        assertFalse(line.contains("=abc"));
    }

    @Test
    public void anythingOddIsReplaced() {
        assertEquals("sso-callback result=other tid=- sfp=- player=-",
                OidcRequestHandler.callbackLine("a b <x>", "name@example.org", "", null));
        assertEquals("sso-callback result=other tid=- sfp=- player=-",
                OidcRequestHandler.callbackLine(null, null, null, null));
    }
}
