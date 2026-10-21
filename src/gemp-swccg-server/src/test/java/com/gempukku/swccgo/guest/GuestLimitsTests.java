package com.gempukku.swccgo.guest;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuestLimitsTests {
    private static final long MINUTE = 60_000;
    private long _now = 1_000_000_000L;

    private GuestLimits limits(int perIpLive, int perIpHour, int globalHour, Set<String> allowlist) {
        GuestSettings d = GuestSettings.defaults();
        GuestSettings s = new GuestSettings(true, d.maxConcurrentGames(), perIpLive, perIpHour, globalHour, allowlist,
                d.unloadIdleMinutes(), d.capEvictIdleMinutes(), d.saveRetentionDays());
        return new GuestLimits(s, () -> _now);
    }

    @Test
    public void GuestLimitsCapLiveGamesPerIp() {
        //test1: an IP with 2 live guest games can't start a third
        //test2: another IP still can
        GuestLimits l = limits(2, 10, 120, Set.of());
        assertTrue(l.tryStart("1.1.1.1", 1).allowed());
        GuestLimits.Decision d = l.tryStart("1.1.1.1", 2);
        assertFalse(d.allowed()); //test1
        assertEquals("too_many_games", d.reason()); //test1
        assertTrue(l.tryStart("2.2.2.2", 0).allowed()); //test2
    }

    @Test
    public void GuestLimitsCapStartsPerIpPerHour() {
        //test1: the 11th start in an hour from one IP is rate limited, with a Retry-After
        //test2: an hour after the first start, the IP may start again
        GuestLimits l = limits(2, 10, 120, Set.of());
        for (int i = 0; i < 10; i++) {
            assertTrue(l.tryStart("1.1.1.1", 0).allowed());
            _now += MINUTE;
        }
        GuestLimits.Decision d = l.tryStart("1.1.1.1", 0);
        assertFalse(d.allowed()); //test1
        assertEquals("rate_limited", d.reason()); //test1
        assertEquals(50 * 60, d.retryAfterSeconds()); //test1

        _now += 50 * MINUTE;
        assertTrue(l.tryStart("1.1.1.1", 0).allowed()); //test2
    }

    @Test
    public void GuestLimitsCapStartsForTheWholeServer() {
        //test1: once the server took its starts for the hour, any IP is rate limited
        //test2: allowlisted IPs skip the per-IP limits but not the server-wide one
        GuestLimits l = limits(2, 10, 3, Set.of("9.9.9.9"));
        assertTrue(l.tryStart("1.1.1.1", 0).allowed());
        assertTrue(l.tryStart("2.2.2.2", 0).allowed());
        assertTrue(l.tryStart("9.9.9.9", 5).allowed()); //test2
        assertFalse(l.tryStart("3.3.3.3", 0).allowed()); //test1
        assertFalse(l.tryStart("9.9.9.9", 0).allowed()); //test2
    }

    @Test
    public void GuestLimitsForgetIpsAfterAnHour() {
        //test1: IPs are dropped from memory an hour after their last start
        GuestLimits l = limits(2, 10, 120, Set.of());
        l.tryStart("1.1.1.1", 0);
        l.tryStart("2.2.2.2", 0);
        _now += 59 * MINUTE;
        l.cleanup();
        assertEquals(2, l.trackedIps());
        _now += MINUTE;
        l.cleanup();
        assertEquals(0, l.trackedIps()); //test1
    }
}
