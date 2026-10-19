package com.gempukku.swccgo.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignupGuardTests {
    @Test
    public void fourthSignupFromOneIpInADayIsRefused() {
        SignupGuard guard = new SignupGuard(3, null);
        assertTrue(guard.allows("203.0.113.5", 0));
        assertTrue(guard.allows("203.0.113.5", 2));
        assertFalse(guard.allows("203.0.113.5", 3));
    }

    @Test
    public void allowlistedVenueIpIsExempt() {
        SignupGuard guard = new SignupGuard(3, "198.51.100.7, 2001:db8::1");
        assertTrue(guard.allows("198.51.100.7", 40));
        assertTrue(guard.allows("2001:DB8:0::1", 40), "allowlist compares normalized IPs");
        assertFalse(guard.allows("198.51.100.8", 3));
    }

    @Test
    public void zeroDisablesTheLimit() {
        assertTrue(new SignupGuard(0, null).allows("203.0.113.5", 1000));
    }

    @Test
    public void honeypotFieldIsNotAnAutofillName() {
        assertEquals("hp_field", SignupGuard.HONEYPOT_FIELD);
    }

    @Test
    public void honeypotTripsOnlyWhenFilled() {
        assertFalse(SignupGuard.isHoneypotTripped(null));
        assertFalse(SignupGuard.isHoneypotTripped(""));
        assertFalse(SignupGuard.isHoneypotTripped("   "));
        assertTrue(SignupGuard.isHoneypotTripped("http://spam.example"));
    }
}
