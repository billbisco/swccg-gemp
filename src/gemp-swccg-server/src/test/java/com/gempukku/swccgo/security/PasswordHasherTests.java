package com.gempukku.swccgo.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTests {
    private final PasswordHasher hasher = new PasswordHasher(1_000);

    @Test
    public void newHashVerifiesOnlyTheRightPassword() {
        String stored = hasher.hash("correct horse");
        assertTrue(stored.startsWith("pbkdf2-sha256$1000$"));
        assertTrue(stored.length() <= 255, "fits the widened password column");
        assertTrue(hasher.verify("correct horse", stored));
        assertFalse(hasher.verify("correct horsE", stored));
        assertFalse(hasher.verify("", stored));
    }

    @Test
    public void samePasswordGetsDifferentSalts() {
        assertNotEquals(hasher.hash("hunter22"), hasher.hash("hunter22"));
    }

    @Test
    public void legacyUnsaltedSha256StillVerifiesAndIsFlaggedForRehash() {
        // sha256("password") as stored by the original GEMP code
        String legacy = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        assertEquals(legacy, PasswordHasher.legacySha256("password"));
        assertTrue(PasswordHasher.isLegacySha256(legacy));
        assertTrue(hasher.verify("password", legacy));
        assertFalse(hasher.verify("Password", legacy));
        assertTrue(hasher.needsRehash(legacy));
    }

    @Test
    public void currentHashDoesNotNeedRehashButWeakerIterationsDo() {
        String current = hasher.hash("pw-12345");
        assertFalse(hasher.needsRehash(current));
        assertTrue(new PasswordHasher(2_000).needsRehash(current));
        assertTrue(new PasswordHasher(2_000).verify("pw-12345", current), "older iteration counts still verify");
    }

    @Test
    public void blankResetMarkerAndMalformedValuesNeverMatch() {
        assertFalse(hasher.verify("", ""));
        assertFalse(hasher.verify("anything", ""));
        assertFalse(hasher.verify("anything", null));
        assertFalse(hasher.verify("!reset", "!reset"));
        assertFalse(hasher.verify("x", "pbkdf2-sha256$abc$$"));
        assertFalse(hasher.verify("x", "pbkdf2-sha256$1000$not_base64!$zz"));
    }

    @Test
    public void passwordPolicyRequiresEightToOneHundredTwentyEightCharacters() {
        assertFalse(PasswordPolicy.isAcceptable(null));
        assertFalse(PasswordPolicy.isAcceptable("1234567"));
        assertTrue(PasswordPolicy.isAcceptable("12345678"));
        assertTrue(PasswordPolicy.isAcceptable("x".repeat(128)));
        assertFalse(PasswordPolicy.isAcceptable("x".repeat(129)));
    }

    @Test
    public void resetCodesAreFormattedNormalizedAndHashed() {
        String code = ResetCodes.newCode();
        assertTrue(code.matches("^[A-Z2-9]{4}-[A-Z2-9]{4}-[A-Z2-9]{4}$"), code);
        assertTrue(ResetCodes.looksLikeCode(code));
        assertTrue(ResetCodes.looksLikeCode(code.toLowerCase().replace("-", " ")));
        assertEquals(ResetCodes.hash(code), ResetCodes.hash(code.toLowerCase()));
        assertNotEquals(ResetCodes.hash(code), ResetCodes.hash(ResetCodes.newCode()));
        assertFalse(ResetCodes.looksLikeCode("password123"));
    }
}
