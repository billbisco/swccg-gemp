package com.gempukku.swccgo.async.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** #504 / #518: where Sign out sends people and which return paths a silent sign-in accepts. */
public class OidcSignOutPathsTests {
    @Test
    public void frontPageFollowsTheCallbackPath() {
        assertEquals("https://swccg.com/play/", OidcRequestHandler.playFrontPage("https://swccg.com/play/gemp-swccg-server/oidc/callback"));
        assertEquals("https://test.swccg.com/gemp-swccg/", OidcRequestHandler.playFrontPage("https://test.swccg.com/gemp-swccg-server/oidc/callback"));
    }

    @Test
    public void returnPathsStayOnPlay() {
        String front = "https://swccg.com/play/";
        assertEquals("/play/hall.html", OidcRequestHandler.safeReturn("/play/hall.html", front));
        assertEquals("/play/game.html?gameId=12&participantId=x", OidcRequestHandler.safeReturn("/play/game.html?gameId=12&participantId=x", front));
        assertNull(OidcRequestHandler.safeReturn("https://evil.example/", front));
        assertNull(OidcRequestHandler.safeReturn("//evil.example/play/", front));
        assertNull(OidcRequestHandler.safeReturn("/play//evil.example", front));
        assertNull(OidcRequestHandler.safeReturn("/play/../login/account", front));
        assertNull(OidcRequestHandler.safeReturn("/forum/", front));
        assertNull(OidcRequestHandler.safeReturn("/play/\\evil", front));
        assertNull(OidcRequestHandler.safeReturn("/play/x\r\nSet-Cookie:a=b", front));
        assertNull(OidcRequestHandler.safeReturn(null, front));
    }
}
