package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Batch B36: findings of the Legacy errata source research (R2 forum, R3 PC news and social, R4 card data) applied to
 * the version cards. Dates come from the PC forum thread "Current Rulings/Errata" (2012-08-21 .. 2013-04-13), the PC
 * news post of 2009-09-21 (Strong Is Vader), the PC home page (Virtual Block 6.3 released 2010-03-12) and the Holotable
 * card data of 2010-05-29 (the "swing" clause of He's No Jedi / Jabba, This Is Your Last Chance!). Imperial Occupation (V)
 * gets its middle version (CBT errata of 2012-09-21, the text of the E-marked PC card image).
 */
public class LegacyBatchB36VersionsTests {
    private static SwccgCardBlueprintLibrary library;

    /** version id -> {printedOn, supersededOn} */
    private static final Map<String, LocalDate[]> DATES = Map.ofEntries(
            Map.entry("2200_7", d(2012, 7, 21, 2012, 8, 21)),
            Map.entry("2200_15", d(2012, 7, 21, 2012, 8, 21)),
            Map.entry("2261_90", d(2009, 9, 4, 2012, 8, 21)),
            Map.entry("2220_111", d(2009, 12, 7, 2012, 9, 21)),
            Map.entry("2220_35", d(2009, 12, 7, 2012, 9, 21)),
            Map.entry("2220_72", d(2009, 12, 7, 2012, 9, 21)),
            Map.entry("2220_1072", d(2012, 9, 21, 2012, 11, 19)),
            Map.entry("2240_36", d(2009, 12, 7, 2012, 10, 19)),
            Map.entry("2261_1010", d(2012, 7, 21, 2012, 10, 19)),
            Map.entry("2273_1036", d(2012, 1, 13, 2012, 10, 19)),
            Map.entry("2274_4", d(2011, 12, 16, 2012, 10, 30)),
            Map.entry("2220_163", d(2009, 12, 7, 2012, 11, 19)),
            Map.entry("2250_26", d(2009, 12, 7, 2012, 11, 19)),
            Map.entry("2220_1156", d(2012, 7, 21, 2012, 11, 19)),
            Map.entry("2220_160", d(2009, 12, 7, 2012, 11, 19)),
            Map.entry("2271_2040", d(2012, 7, 21, 2013, 4, 13)),
            Map.entry("2230_109", d(2009, 12, 7, 2013, 4, 13)),
            Map.entry("2210_6", d(2009, 12, 7, 2013, 4, 13)),
            Map.entry("2283_3", d(2013, 3, 28, 2013, 4, 13)),
            Map.entry("2261_87", d(2009, 9, 4, 2009, 9, 21)),
            Map.entry("2261_1087", d(2009, 9, 21, 2012, 7, 21)),
            Map.entry("2261_35", d(2009, 9, 4, 2010, 5, 29)),
            Map.entry("2261_80", d(2009, 9, 4, 2010, 5, 29)),
            Map.entry("2263_1", d(2010, 3, 12, 2013, 4, 27)),
            Map.entry("2263_6", d(2010, 3, 12, 2013, 4, 27)),
            Map.entry("2263_7", d(2010, 3, 12, 2013, 4, 27)),
            Map.entry("2263_9", d(2010, 3, 12, 2012, 7, 21)),
            Map.entry("2263_21", d(2010, 3, 12, 2012, 7, 21)));

    private static LocalDate[] d(int y1, int m1, int d1, int y2, int m2, int d2) {
        return new LocalDate[]{LocalDate.of(y1, m1, d1), LocalDate.of(y2, m2, d2)};
    }

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void DatesFollowTheDatedSources() {
        assertEquals(28, DATES.size());
        DATES.forEach((id, dates) -> {
            var card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion v = card.getLegacyVersion();
            assertNotNull(id, v);
            assertEquals(id + " printedOn", dates[0], v.printedOn());
            assertEquals(id + " supersededOn", dates[1], v.supersededOn());
        });
    }

    @Test
    public void ImperialOccupationHasThreeVersions() {
        assertEquals("2220_1072", LegacyVersion.versionIdOf("1220_72", 1));
        assertEquals("1220_72", LegacyVersion.currentPrintingIdOf("2220_1072"));
        assertNull(library.getSwccgoCardBlueprint("1220_72").getLegacyVersion());
        assertEquals("", library.getSwccgoCardBlueprint("2220_72").getLegacyVersion().label());
        assertEquals("PC-E1", library.getSwccgoCardBlueprint("2220_1072").getLegacyVersion().label());
        assertNotNull(library.getSwccgoCardBlueprint("2220_1072_BACK"));
        assertEquals(LocalDate.of(2012, 9, 21), library.getSwccgoCardBlueprint("2220_72_BACK").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 11, 19), library.getSwccgoCardBlueprint("2220_1072_BACK").getLegacyVersion().supersededOn());
    }

    @Test
    public void SixPointThreeSourcesNameTheReleaseDate() {
        for (String id : new String[]{"2263_1", "2263_6", "2263_7", "2263_9", "2263_21"})
            assertEquals(id, true, library.getSwccgoCardBlueprint(id).getLegacyVersion().source().contains("released 2010-03-12"));
    }
}
