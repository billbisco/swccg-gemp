package com.gempukku.swccgo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginThrottleTests {
    @Test
    public void oneIpSprayingManyNamesIsBlockedForTheWindow() {
        LoginThrottle throttle = new LoginThrottle(10, 5, 60_000, 30_000);
        long t = 1_000_000;
        for (int i = 0; i < 5; i++)
            throttle.recordFailure("name" + i, "203.0.113.9", t);
        assertTrue(throttle.isBlocked("someoneElse", "203.0.113.9", t + 1));
        assertFalse(throttle.isBlocked("someoneElse", "203.0.113.10", t + 1), "other IPs unaffected");
        assertFalse(throttle.isBlocked("someoneElse", "203.0.113.9", t + 60_001), "window rolls over");
    }

    @Test
    public void accountLockIsCaseInsensitiveAndClearedBySuccess() {
        LoginThrottle throttle = new LoginThrottle(3, 1000, 60_000, 30_000);
        long t = 5_000_000;
        throttle.recordFailure("Bill", "a", t);
        throttle.recordFailure("bill", "b", t);
        throttle.recordFailure("BILL", "c", t);
        assertTrue(throttle.isBlocked("Bill", "d", t + 10));
        assertFalse(throttle.isBlocked("Bill", "d", t + 30_001), "lock lasts lockMillis from the last failure");
        throttle.recordSuccess("Bill", "d");
        assertFalse(throttle.isBlocked("Bill", "d", t + 10));
    }
}
