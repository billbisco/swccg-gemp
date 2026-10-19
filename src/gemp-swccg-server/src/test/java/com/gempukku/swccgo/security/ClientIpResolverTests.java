package com.gempukku.swccgo.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientIpResolverTests {
    private final ClientIpResolver resolver = new ClientIpResolver(ClientIpResolver.DEFAULT_TRUSTED);

    @Test
    public void directClientCannotSpoofForwardedFor() {
        assertEquals("203.0.113.5", resolver.resolve("203.0.113.5", "1.2.3.4"));
    }

    @Test
    public void behindNginxTheRightmostUntrustedHopIsTheClient() {
        // nginx $proxy_add_x_forwarded_for: "<whatever the client sent>, <real client>"
        assertEquals("198.51.100.20", resolver.resolve("172.18.0.1", "6.6.6.6, 198.51.100.20"));
        assertEquals("198.51.100.20", resolver.resolve("172.18.0.1", "198.51.100.20"));
    }

    @Test
    public void injectedHtmlOrSqlInTheHeaderIsNeverReturned() {
        assertEquals("172.18.0.1", resolver.resolve("172.18.0.1", "<img src=x onerror=alert(1)>"));
        assertEquals("172.18.0.1", resolver.resolve("172.18.0.1", "1.2.3.4') OR 1=1 -- "));
        assertEquals("172.18.0.1", resolver.resolve("172.18.0.1", "evil.example.com"));
    }

    @Test
    public void ipv6AndMappedIpv4AreNormalized() {
        assertEquals("2001:db8::1", resolver.resolve("127.0.0.1", "2001:DB8:0:0::1"));
        assertEquals("203.0.113.9", resolver.resolve("::ffff:203.0.113.9", null));
    }

    @Test
    public void customTrustListIsHonored() {
        ClientIpResolver onlyLoopback = new ClientIpResolver("127.0.0.1/32");
        assertEquals("172.18.0.1", onlyLoopback.resolve("172.18.0.1", "198.51.100.20"));
        assertEquals("198.51.100.20", onlyLoopback.resolve("127.0.0.1", "198.51.100.20"));
    }

    @Test
    public void badTrustConfigFallsBackToTheDefault() {
        ClientIpResolver broken = new ClientIpResolver("${PROXY_TRUSTED:-not-an-ip}");
        assertEquals("198.51.100.20", broken.resolve("172.18.0.1", "198.51.100.20"));
    }
}
