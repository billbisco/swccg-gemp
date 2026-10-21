package com.gempukku.swccgo.guest;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuestIdentityTests {

    @Test
    public void GuestTokensAreRandomAndWellFormed() {
        //test1: a token is 32 letters and digits and is recognised as a token
        //test2: 1,000 tokens are all different
        String token = GuestIdentity.newToken();
        assertEquals(32, token.length()); //test1
        assertTrue(token.matches("[A-Za-z0-9]{32}")); //test1
        assertTrue(GuestIdentity.isToken(token)); //test1

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            seen.add(GuestIdentity.newToken());
        }
        assertEquals(1000, seen.size()); //test2
    }

    @Test
    public void GuestIdComesFromTheTokenAndCanNeverBeAnAccountName() {
        //test1: the same token always gives the same id (a restart finds the guest's games again)
        //test2: different tokens give different ids
        //test3: the id starts with "guest-", which registration refuses, and is longer than any account name
        String token = GuestIdentity.newToken();
        String id = GuestIdentity.guestIdFor(token);
        assertEquals(id, GuestIdentity.guestIdFor(token)); //test1
        assertNotEquals(id, GuestIdentity.guestIdFor(GuestIdentity.newToken())); //test2
        assertTrue(id.startsWith("guest-")); //test3
        assertTrue(GuestIdentity.isGuestId(id)); //test3
        assertTrue(id.length() > 10); //test3
        assertFalse(GuestIdentity.isGuestId("bill")); //test3
    }

    @Test
    public void GuestCookieValuesThatAreNotOurTokensAreRefused() {
        //test1: wrong length, odd characters and null are not tokens
        //test2: turning one into a guest id fails
        assertFalse(GuestIdentity.isToken(null)); //test1
        assertFalse(GuestIdentity.isToken("short")); //test1
        assertFalse(GuestIdentity.isToken("A".repeat(31) + ";")); //test1
        assertFalse(GuestIdentity.isToken("A".repeat(33))); //test1
        assertThrows(IllegalArgumentException.class, () -> GuestIdentity.guestIdFor("../../etc/passwd")); //test2
    }
}
