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

    /** The output filter (#249, t129u): what a name can never hold goes; grandfathered names show as stored. */
    @Test
    public void forDisplay() {
        String[][] cases = {
                {"Luke Skywalker", "Luke Skywalker"},
                {"luke_sky-walker 2", "luke_sky-walker 2"},
                {" Luke  Sky__walker_ ", " Luke  Sky__walker_ "},   // spacing is never tidied for display
                {"Zoë Ångström", "Zoë Ångström"},
                {"Zoe\u0308", "Zoe\u0308"},
                {"\u0308Zoe", "Zoe"},
                {"Владимир", "Владимир"}, {"李小龍", "李小龍"}, {"محمد", "محمد"},
                {"Han\uD83D\uDE00Solo", "HanSolo"},
                {"T\uD835\uDC00x", "Tx"},
                {"<b>Leia</b>", "bLeiab"},
                {"Leia\"'&#;:/@[]{}|", "Leia"},
                {"Rey\u200B\u202E\uFE0F\u00AD", "Rey"},
                {"Obi\u2013Wan\u00A0Kenobi", "Obi-Wan Kenobi"},
                {"Bad\u0000\u001b[31mName", "Bad31mName"},
                {"Snow\u2603man\u2122", "Snowman"},
                {"~OzzelBot", "~OzzelBot"}, {"~Rando_Cal", "~Rando_Cal"},
                {"Light Side", "Light Side"},
                {"\uD83D\uDE00\uD83D\uDE00", PlayerNames.DISPLAY_PLACEHOLDER},
                {" _-", PlayerNames.DISPLAY_PLACEHOLDER},
                {"", PlayerNames.DISPLAY_PLACEHOLDER},
                {null, PlayerNames.DISPLAY_PLACEHOLDER},
        };
        for (String[] c : cases)
            assertEquals(c[1], PlayerNames.forDisplay(c[0]), String.valueOf(c[0]));
        for (String n : new String[]{"R", "Luke Sky", "Luke_Sky", "José Núñez", "김민준", "Ana María de la Cruz Solo Organa"})
            assertEquals(n, PlayerNames.forDisplay(n), "every well-formed name shows unchanged");
    }

    /** A code point's class from Java's own Character data, the way the name table defines it. */
    private static char javaClass(int cp) {
        int type = Character.getType(cp);
        if (type == Character.DASH_PUNCTUATION)
            return PlayerNames.DASH;
        if (Character.isSpaceChar(cp) || (cp >= 0x09 && cp <= 0x0D) || cp == 0x85)   // White_Space
            return PlayerNames.SPACE;
        if (type == Character.FORMAT || (cp >= 0xFE00 && cp <= 0xFE0F) || (cp >= 0x180B && cp <= 0x180F)
                || cp == 0x034F || cp == 0x115F || cp == 0x1160 || cp == 0x17B4 || cp == 0x17B5 || cp == 0x2065
                || cp == 0x3164 || cp == 0xFFA0 || (cp >= 0xFFF0 && cp <= 0xFFF8))     // Variation_Selector, ODI
            return PlayerNames.INVISIBLE;
        if (Character.isLetter(cp) || type == Character.DECIMAL_DIGIT_NUMBER)
            return PlayerNames.ALNUM;
        if (type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK)
            return PlayerNames.MARK;
        return PlayerNames.OTHER;
    }

    /** The name table is Unicode 15.0, Java 21's own version: it agrees with Character on every BMP code point. */
    @Test
    public void tableIsJavaUnicode() {
        int differ = 0;
        for (int cp = 0; cp <= 0xFFFF; cp++) {
            char t = PlayerNames.classOf(cp), j = javaClass(cp);
            if (t != j) {
                // a newer JDK knows characters added after 15.0; the table keeps calling them other
                assertEquals(PlayerNames.OTHER, t, "U+" + Integer.toHexString(cp));
                differ++;
            }
        }
        if (Runtime.version().feature() == 21)
            assertEquals(0, differ);
        assertEquals(PlayerNames.OTHER, PlayerNames.classOf(0x10000));
    }

    /**
     * Letters and marks added in Unicode 16.0 and 17.0 (Go and browsers know them, Java 21 does not) are refused
     * and not shown, the same way the sign-in service strips them (t130u).
     */
    @Test
    public void newerThan15Refused() {
        for (String n : new String[]{"Han\u1C89Solo", "Luke\uA7CB", "Leia\uA7F1", "Ben\u0C5C", "Ra\u0897y",
                "Rey\u1ACF", "\u1C89"})
            assertFalse(PlayerNames.isWellFormed(n), n);
        assertEquals("HanSolo", PlayerNames.forDisplay("Han\u1C89Solo"));
        assertEquals("Ray", PlayerNames.forDisplay("Ra\u0897y"));
        assertEquals(PlayerNames.DISPLAY_PLACEHOLDER, PlayerNames.forDisplay("\u1C89\u1C8A"));
        assertTrue(PlayerNames.isWellFormed("\uA7C0\u1C80\u0CDD"));   // letters already in 15.0
        assertFalse(PlayerNames.isWellFormed("Han\u3164Solo"));         // a Hangul filler is invisible, not a letter
    }
}
