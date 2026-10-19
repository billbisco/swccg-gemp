package com.gempukku.swccgo.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OriginPolicyTests {
    private final OriginPolicy policy = new OriginPolicy("^.*$"); // the shipped property value = built-in default

    @Test
    public void ourSitesMayPost() {
        assertTrue(policy.allows("POST", "https://play.swccg.com", null));
        assertTrue(policy.allows("POST", "https://swccg.com", null));
        assertTrue(policy.allows("POST", "https://test.swccg.com", null));
        assertTrue(policy.allows("POST", null, "https://play.swccg.com/gemp-swccg/admin.html"));
        assertTrue(policy.allows("POST", "http://localhost:17001", null));
    }

    @Test
    public void otherSitesCannotPost() {
        assertFalse(policy.allows("POST", "https://evil.example", null));
        assertFalse(policy.allows("POST", "https://swccg.com.evil.example", null));
        assertFalse(policy.allows("POST", "http://play.swccg.com", null), "plain http origin is not ours");
        assertFalse(policy.allows("POST", null, "https://evil.example/page"));
        assertFalse(policy.allows("POST", "null", null), "sandboxed or data: origin");
    }

    @Test
    public void nonBrowserClientsWithoutOriginOrRefererStillWork() {
        assertTrue(policy.allows("POST", null, null));
    }

    @Test
    public void getRequestsOnlyCheckAnOriginWhenPresent() {
        assertTrue(policy.allows("GET", null, "https://evil.example/"));
        assertFalse(policy.allows("GET", "https://evil.example", null));
    }

    @Test
    public void explicitPatternOverridesTheDefault() {
        OriginPolicy custom = new OriginPolicy("^https://gemp\\.example\\.org$");
        assertTrue(custom.allows("POST", "https://gemp.example.org", null));
        assertFalse(custom.allows("POST", "https://play.swccg.com", null));
    }
}
