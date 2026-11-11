package com.gempukku.swccgo.async.oidc;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;

public class InternalUrlTest {
    @Test
    public void ordinaryHostUnchanged() throws Exception {
        URI u = URI.create("http://login.test:8091/internal/v1/name-check");
        assertSame(u, InternalUrl.reachable(u));
    }

    @Test
    public void underscoreHostIsCalledByAddress() throws Exception {
        // HttpClient refuses this as written; "localhost" stands in for a container name the resolver knows.
        URI u = URI.create("http://local_host:8091/internal/v1/revoke");
        assertThrows(IllegalArgumentException.class, () -> HttpRequest.newBuilder(u).build());
        URI r = InternalUrl.reachable(URI.create("http://localhost:8091/internal/v1/revoke"));
        assertEquals("/internal/v1/revoke", r.getPath());
        assertEquals(8091, r.getPort());
        HttpRequest.newBuilder(r).build();
    }
}
