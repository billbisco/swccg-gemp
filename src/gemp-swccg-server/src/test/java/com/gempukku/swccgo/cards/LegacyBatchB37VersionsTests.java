package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Batch B37: version dates from the Legacy errata source research R5 (the PC forum rulings threads t=48201 and
 * t=36574, 2010-07 .. 2014-01) and R1 (Wayback sweep of the PC website: "Changes to Floating Refinery (v)", 2012-10-30).
 * The forum errata posts date many versions that the record dated only by the PC website card list (2012-07-21),
 * Advanced Rulebook 2.0 (2012-08-27) or the Master List ("official text as of 2013-04-27", but compiled until 2014-02).
 * Dates only: no text changes. Errata "legal as of" a later day use that day (2012-04-11 post, legal 2012-04-20);
 * reposts and "updated errata" give an upper bound (Imperial Atrocity (V) / Imperial Propaganda (V) by 2013-06-14,
 * Maneuvering Flaps (V) by 2012-02-07).
 */
public class LegacyBatchB37VersionsTests {
    private static SwccgCardBlueprintLibrary library;

    /** version id -> {printedOn, supersededOn} */
    private static final Map<String, LocalDate[]> DATES = Map.ofEntries(
            Map.entry("2250_41", d(2009, 12, 7, 2010, 7, 27)),
            Map.entry("2261_63", d(2009, 9, 4, 2010, 10, 8)),
            Map.entry("2220_50", d(2009, 12, 7, 2010, 12, 8)),
            Map.entry("2250_28", d(2009, 12, 7, 2010, 12, 20)),
            Map.entry("2272_5", d(2010, 12, 15, 2011, 1, 28)),
            Map.entry("2272_22", d(2010, 12, 15, 2011, 1, 28)),
            Map.entry("2271_2", d(2010, 6, 18, 2011, 3, 21)),
            Map.entry("2230_7", d(2009, 12, 7, 2011, 3, 21)),
            Map.entry("2262_7", d(2009, 12, 7, 2011, 3, 21)),
            Map.entry("2250_44", d(2009, 12, 7, 2011, 3, 21)),
            Map.entry("2210_38", d(2009, 12, 7, 2011, 3, 21)),
            Map.entry("2273_41", d(2011, 6, 22, 2012, 3, 15)),
            Map.entry("2273_1041", d(2011, 6, 3, 2011, 6, 22)),
            Map.entry("2272_23", d(2010, 12, 15, 2011, 6, 22)),
            Map.entry("2273_27", d(2011, 6, 22, 2012, 4, 20)),
            Map.entry("2273_1027", d(2011, 6, 3, 2011, 6, 22)),
            Map.entry("2273_33", d(2011, 6, 3, 2011, 6, 22)),
            Map.entry("2273_1033", d(2011, 6, 22, 2011, 7, 25)),
            Map.entry("2210_80", d(2009, 12, 7, 2011, 7, 25)),
            Map.entry("2271_38", d(2010, 7, 28, 2011, 8, 26)),
            Map.entry("2240_101", d(2009, 12, 7, 2011, 8, 26)),
            Map.entry("2250_22", d(2009, 12, 7, 2011, 8, 26)),
            Map.entry("2240_58", d(2009, 12, 7, 2011, 9, 12)),
            Map.entry("2240_58_BACK", d(2009, 12, 7, 2011, 9, 12)),
            Map.entry("2240_1058", d(2011, 9, 12, 2013, 4, 27)),
            Map.entry("2240_1058_BACK", d(2011, 9, 12, 2013, 4, 27)),
            Map.entry("2273_36", d(2011, 6, 3, 2012, 1, 13)),
            Map.entry("2273_1036", d(2012, 1, 13, 2012, 10, 19)),
            Map.entry("2271_39", d(2010, 6, 18, 2012, 1, 19)),
            Map.entry("2273_19", d(2011, 6, 3, 2012, 1, 30)),
            Map.entry("2220_128", d(2009, 12, 7, 2012, 2, 7)),
            Map.entry("2261_65", d(2009, 9, 4, 2012, 2, 21)),
            Map.entry("2274_18", d(2011, 12, 16, 2012, 4, 20)),
            Map.entry("2210_43", d(2009, 12, 7, 2012, 4, 11)),
            Map.entry("2230_77", d(2009, 12, 7, 2012, 7, 13)),
            Map.entry("2250_82", d(2009, 12, 7, 2012, 8, 4)),
            Map.entry("2250_82_BACK", d(2009, 12, 7, 2012, 8, 4)),
            Map.entry("2240_86", d(2009, 12, 7, 2013, 6, 14)),
            Map.entry("2240_27", d(2009, 12, 7, 2013, 6, 14)),
            Map.entry("2220_136", d(2009, 12, 7, 2013, 7, 24)),
            Map.entry("2283_21", d(2013, 3, 28, 2013, 7, 24)),
            Map.entry("2273_1013", d(2012, 7, 21, 2013, 7, 24)),
            Map.entry("2220_68", d(2009, 12, 7, 2013, 8, 1)),
            Map.entry("2230_13", d(2009, 12, 7, 2013, 12, 30)),
            Map.entry("2250_34", d(2009, 12, 7, 2014, 1, 3)),
            Map.entry("2210_107", d(2009, 12, 7, 2014, 1, 13)),
            Map.entry("2274_4", d(2011, 12, 16, 2012, 10, 30)));

    private static LocalDate[] d(int y1, int m1, int d1, int y2, int m2, int d2) {
        return new LocalDate[]{LocalDate.of(y1, m1, d1), LocalDate.of(y2, m2, d2)};
    }

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void DatesFollowTheDatedSources() {
        assertEquals(47, DATES.size());
        DATES.forEach((id, dates) -> {
            var card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion v = card.getLegacyVersion();
            assertNotNull(id, v);
            assertEquals(id + " printedOn", dates[0], v.printedOn());
            assertEquals(id + " supersededOn", dates[1], v.supersededOn());
            assertTrue(id + " printed before superseded", !v.printedOn().isAfter(v.supersededOn()));
        });
    }

    @Test
    public void SourcesNameTheForumOrNewsPost() {
        for (String id : new String[]{"2250_41", "2220_50", "2272_5", "2272_22", "2210_38", "2273_1013", "2210_107", "2240_1058"})
            assertTrue(id, library.getSwccgoCardBlueprint(id).getLegacyVersion().source().contains("PC forum"));
        assertTrue(library.getSwccgoCardBlueprint("2274_4").getLegacyVersion().source().contains("Changes to Floating Refinery (v)"));
    }
}
