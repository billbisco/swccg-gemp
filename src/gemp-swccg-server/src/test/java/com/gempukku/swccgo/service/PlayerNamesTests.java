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

    @Test
    public void reserved() {
        for (String n : new String[]{"Light Side", "Light_Side", "Dark Side", "Dark_Side", "light side", "DARK_SIDE",
                "admin", "Ad min", "ad_min", "Guest 7", "System", "bye"})
            assertTrue(PlayerNames.isReserved(n), n);
        for (String n : new String[]{"Light Side 2", "Light", "Dark", "Luke Sky", "Padmin"})
            assertFalse(PlayerNames.isReserved(n), n);
    }
}
