package com.gempukku.swccgo.async.oidc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Review M5: a flood of /oidc/start can't block real sign-ins. */
public class PendingStartsTest {
    @Test
    public void oneIpCannotHoldMoreThanItsShare() {
        PendingStarts<String> p = new PendingStarts<>(1000, 3, 600_000);
        for (int i = 0; i < 3; i++)
            assertTrue(p.put("s" + i, "v", "203.0.113.9", 0));
        assertFalse(p.put("s3", "v", "203.0.113.9", 0), "fourth open start from one IP");
        assertTrue(p.put("t0", "v", "198.51.100.1", 0), "another IP still gets in");
        assertEquals("v", p.take("s0", 1));
        assertTrue(p.put("s4", "v", "203.0.113.9", 1), "finishing one frees a slot");
    }

    @Test
    public void fullTableEvictsOldestInsteadOfRefusing() {
        PendingStarts<String> p = new PendingStarts<>(3, 3, 600_000);
        p.put("a", "A", "10.0.0.1", 0);
        p.put("b", "B", "10.0.0.2", 1);
        p.put("c", "C", "10.0.0.3", 2);
        assertTrue(p.put("d", "D", "10.0.0.4", 3));
        assertEquals(3, p.size());
        assertNull(p.take("a", 4), "oldest was evicted");
        assertEquals("D", p.take("d", 4));
    }

    @Test
    public void expiredStartsAreRefusedAndFreed() {
        PendingStarts<String> p = new PendingStarts<>(10, 1, 100);
        assertTrue(p.put("a", "A", "10.0.0.1", 0));
        assertNull(p.take("a", 1000), "expired");
        assertTrue(p.put("b", "B", "10.0.0.1", 1000), "slot freed after expiry");
        assertNull(p.take("unknown", 1000));
        assertNull(p.take(null, 1000));
        assertEquals("B", p.take("b", 1001));
        assertNull(p.take("b", 1001), "single use");
    }
}
