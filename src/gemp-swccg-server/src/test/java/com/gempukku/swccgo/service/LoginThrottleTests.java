package com.gempukku.swccgo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginThrottleTests {
    @Test
    public void oneIpSprayingManyNamesIsBlockedForTheWindow() {
        LoginThrottle throttle = new LoginThrottle(10, 100, 5, 60_000, 30_000);
        long t = 1_000_000;
        for (int i = 0; i < 5; i++)
            throttle.recordFailure("name" + i, "203.0.113.9", t);
        assertTrue(throttle.isBlocked("someoneElse", "203.0.113.9", t + 1));
        assertFalse(throttle.isBlocked("someoneElse", "203.0.113.10", t + 1), "other IPs unaffected");
        assertFalse(throttle.isBlocked("someoneElse", "203.0.113.9", t + 60_001), "window rolls over");
    }

    @Test
    public void pairLockIsCaseInsensitiveAndOnlyForThatIp() {
        LoginThrottle throttle = new LoginThrottle(3, 100, 1000, 60_000, 30_000);
        long t = 5_000_000;
        throttle.recordFailure("Bill", "a", t);
        throttle.recordFailure("bill", "a", t);
        throttle.recordFailure("BILL", "a", t);
        assertTrue(throttle.isBlocked("Bill", "a", t + 10));
        assertFalse(throttle.isBlocked("Bill", "b", t + 10), "same account from another IP is fine");
        assertFalse(throttle.isBlocked("Bill", "a", t + 30_001), "lock lasts lockMillis from the last failure");
    }

    @Test
    public void failuresSpreadOverIpsSlowTheNameButNeverLockIt() {
        LoginThrottle throttle = new LoginThrottle(3, 6, 1000, 60_000, 30_000);
        long t = 7_000_000;
        for (int i = 0; i < 5; i++)
            throttle.recordFailure("Bill", "ip" + i, t);
        assertEquals(0, throttle.delayMillis("Bill", t + 1), "below the account ceiling");
        throttle.recordFailure("Bill", "ip5", t);
        assertEquals(LoginThrottle.DELAY_STEP_MILLIS, throttle.delayMillis("Bill", t + 1));
        for (int i = 6; i < 100; i++)
            throttle.recordFailure("Bill", "ip" + i, t);
        assertEquals(LoginThrottle.MAX_DELAY_MILLIS, throttle.delayMillis("Bill", t + 1), "capped");
        assertFalse(throttle.isBlocked("Bill", "fresh", t + 1), "never locked on an IP that did not guess (#564)");
        assertEquals(0, throttle.delayMillis("Alice", t + 1));
    }

    @Test
    public void successClearsTheAccountOnEveryIp() {
        LoginThrottle throttle = new LoginThrottle(2, 4, 1000, 60_000, 30_000);
        long t = 9_000_000;
        throttle.recordFailure("Bill", "a", t);
        throttle.recordFailure("Bill", "a", t);
        throttle.recordFailure("Bill", "b", t);
        throttle.recordFailure("Bill", "b", t);
        assertTrue(throttle.isBlocked("Bill", "a", t + 1));
        assertFalse(throttle.isBlocked("Bill", "c", t + 1), "no account-wide lock (#564)");
        assertEquals(LoginThrottle.DELAY_STEP_MILLIS, throttle.delayMillis("Bill", t + 1));
        throttle.recordSuccess("bill", null);
        assertEquals(0, throttle.delayMillis("Bill", t + 1));
        assertFalse(throttle.isBlocked("Bill", "a", t + 1));
        assertFalse(throttle.isBlocked("Bill", "b", t + 1));
        assertFalse(throttle.isBlocked("Bill", "c", t + 1));
    }
}
