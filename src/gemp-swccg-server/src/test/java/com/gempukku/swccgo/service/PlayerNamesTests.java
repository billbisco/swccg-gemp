package com.gempukku.swccgo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The site's new-name rule on Play (#249; t121u, t122u). */
public class PlayerNamesTests {
    @Test
    public void wellFormed() {
        for (String n : new String[]{"R", "Luke Sky", "Luke_Sky", "luke sky", "Obi-Wan", "José Núñez", "Zoë", "李小龍",
                "Владимир", "Ana María de la Cruz Solo Organa", "x-", "-x", "a_b c"})
            assertTrue(PlayerNames.isWellFormed(n), n);
        for (String n : new String[]{"", " Luke", "Luke ", "_Luke", "Luke_", "Luke  Sky", "Luke _Sky", "Luke__Sky",
                "Ana María de la Cruz Solo Organa2", "Luke!", "a<b", "a@b", "a\tb", "a\u200bb", "---",
                "\uD835\uDC00bc", "Jose\u0301", null})
            assertFalse(PlayerNames.isWellFormed(n), String.valueOf(n));
    }

    /** Play stores names as 3-byte UTF-8 (utf8mb3; t124u): nothing above U+FFFF is a new name (t127u). */
    @Test
    public void fourByteRefused() {
        for (String n : new String[]{
                "Luke\uD835\uDC00",              // MATHEMATICAL BOLD CAPITAL A
                "\uD835\uDC0B\uD835\uDC2E\uD835\uDC24\uD835\uDC1E", // math bold lookalike of a Latin name
                "Han \uD840\uDC00 Solo",        // CJK Extension B letter
                "\uD840\uDC00",
                "R\uD835\uDFD0",                 // MATHEMATICAL BOLD DIGIT TWO
                "Leia\uD834\uDD67",              // supplementary combining mark
                "Rey \uD83D\uDE80"})             // emoji
            assertFalse(PlayerNames.isWellFormed(n), n);
        for (String n : new String[]{"김민준", "さくら", "カタカナ", "山田 太郎", "José Núñez", "Владимир", "محمد"})
            assertTrue(PlayerNames.isAllowedNewName(n), n);
    }

    @Test
    public void reserved() {
        for (String n : new String[]{"Light Side", "Light_Side", "Dark Side", "Dark_Side", "light side", "DARK_SIDE",
                "admin", "Ad min", "ad_min", "Guest 7", "System", "bye"})
            assertTrue(PlayerNames.isReserved(n), n);
        for (String n : new String[]{"Light Side 2", "Light", "Dark", "Luke Sky", "Padmin"})
            assertFalse(PlayerNames.isReserved(n), n);
    }
}
