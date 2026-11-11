package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B41 (Bill's ruling of 2026-11-11, ai-team-skills shared/gemp-legacy-cards/RULINGS.md: every published text is
 * a version, even a word-for-word repeat). Luke's Blaster Pistol (forum errata 2011-08-26 and 2012-04-11), Dark Waters
 * (Execution Arena errata 2012-05-09, card list), Phylo Gandish (Virtual Block 4 added 2013-10-30), Mos Eisley (interior
 * struck 2014-03-27), AT-RT (first-release, sheet, 2014-07-23 and 2014-07-25 texts), Aggressive Negotiations (7 side
 * 2014-08-08), Rycar Ryjerd (2014-03-27), the A Step Backward / Dark Forces title errata (2014-05-02), Kessel Spice Mines
 * (Episode I icon covered 2011-07-25), Kessel Surveillance System, Imperial Navigation Charts and Scoundrel's Luck (forum
 * errata 2011/2012, card list), Geonosis Council Room ("may" struck 2014-07-09), I'm Sorry (CBT errata 2013-10-09),
 * Leave Them To Me (both printings: card-list "Planet") and Armed And Dangerous (title order 2011-07-14). Rework for Bill's
 * rulings of 2026-11-11 08:45/09:14: every PC card image (server folder date) and Master List text is a version of its own;
 * Scoundrel's Luck's PC card image (2013-05-07) is the latest, so the current card has no "(even if inactive)". Images are pinned in
 * cardImageUrlTests.js. 601_101 / 601_111 are the current classes of 1210_136 / 1281_32.
 */
public class LegacyBatchB41VersionsTests {
    /** version id -> {current printing id, label, printed on, superseded on, printed title or null} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2220_169", new String[]{"1220_169", "", "2009-12-07", "2011-08-26", null}),
            Map.entry("2220_2169", new String[]{"1220_169", "PC-E1", "2011-08-26", "2012-04-11", null}),
            Map.entry("2220_3169", new String[]{"1220_169", "PC-E2", "2012-04-11", "2012-07-21", null}),
            Map.entry("2220_1169", new String[]{"1220_169", "PC-E3", "2012-07-21", "2013-02-25", null}),
            Map.entry("2220_4169", new String[]{"1220_169", "PC-E4", "2013-02-25", "2013-04-27", null}),
            Map.entry("2261_33", new String[]{"1261_33", "", "2009-09-04", "2010-07-26", null}),
            Map.entry("2261_1033", new String[]{"1261_33", "PC-E1", "2010-07-26", "2010-07-27", null}),
            Map.entry("2261_2033", new String[]{"1261_33", "PC-E2", "2010-07-27", "2012-05-09", null}),
            Map.entry("2261_3033", new String[]{"1261_33", "PC-E3", "2012-05-09", "2012-07-21", null}),
            Map.entry("2261_4033", new String[]{"1261_33", "PC-E4", "2012-07-21", "2013-02-25", null}),
            Map.entry("2261_5033", new String[]{"1261_33", "PC-E5", "2013-02-25", "2013-04-27", null}),
            Map.entry("2263_26", new String[]{"1263_26", "", "2010-03-12", "2013-02-25", null}),
            Map.entry("2263_1026", new String[]{"1263_26", "PC-E1", "2013-02-25", "2013-04-27", null}),
            Map.entry("2263_2026", new String[]{"1263_26", "PC-E2", "2013-04-27", "2013-10-30", null}),
            Map.entry("2210_73", new String[]{"1210_73", "", "2009-12-07", "2013-02-25", null}),
            Map.entry("2210_1073", new String[]{"1210_73", "PC-E1", "2013-02-25", "2013-04-27", null}),
            Map.entry("2210_2073", new String[]{"1210_73", "PC-E2", "2013-04-27", "2014-03-27", null}),
            Map.entry("2240_55", new String[]{"1240_55", "", "2009-12-07", "2011-07-25", null}),
            Map.entry("2240_1055", new String[]{"1240_55", "PC-E1", "2011-07-25", "2013-02-25", null}),
            Map.entry("2240_2055", new String[]{"1240_55", "PC-E2", "2013-02-25", "2013-04-27", null}),
            Map.entry("2291_1071", new String[]{"1291_71", "PC-E1", "2014-01-22", "2014-02-03", null}),
            Map.entry("2291_3071", new String[]{"1291_71", "PC-E2", "2014-02-03", "2014-02-18", null}),
            Map.entry("2291_71", new String[]{"1291_71", "PC-E3", "2014-02-18", "2014-07-23", null}),
            Map.entry("2291_2071", new String[]{"1291_71", "PC-E4", "2014-07-23", "2014-07-25", null}),
            Map.entry("2291_69", new String[]{"1291_69", "", "2014-01-10", "2014-02-03", null}),
            Map.entry("2291_69_BACK", new String[]{"1291_69_BACK", "", "2014-01-10", "2014-02-03", null}),
            Map.entry("2291_1069", new String[]{"1291_69", "PC-E1", "2014-02-03", "2014-02-18", null}),
            Map.entry("2291_1069_BACK", new String[]{"1291_69_BACK", "PC-E1", "2014-02-03", "2014-02-18", null}),
            Map.entry("2291_2069", new String[]{"1291_69", "PC-E2", "2014-02-18", "2014-08-08", null}),
            Map.entry("2291_2069_BACK", new String[]{"1291_69_BACK", "PC-E2", "2014-02-18", "2014-08-08", null}),
            Map.entry("2210_136", new String[]{"1210_136", "", "2009-12-07", "2012-07-21", null}),
            Map.entry("2210_1136", new String[]{"1210_136", "PC-E2", "2013-02-25", "2013-04-27", null}),
            Map.entry("2210_2136", new String[]{"1210_136", "PC-E3", "2013-04-27", "2014-03-27", null}),
            Map.entry("2282_23", new String[]{"1282_23", "", "2012-09-14", "2012-12-12", "Found Someone You Have & Higher Ground"}),
            Map.entry("2282_1023", new String[]{"1282_23", "PC-E2", "2013-04-27", "2013-05-07", "Found Someone You Have & Higher Ground"}),
            Map.entry("2282_2023", new String[]{"1282_23", "PC-E3", "2013-05-07", "2014-05-02", "Found Someone You Have & Higher Ground"}),
            Map.entry("2291_9", new String[]{"1291_9", "", "2014-01-10", "2014-02-03", "Begin Landing Your Troops & The Dark Path"}),
            Map.entry("2291_1009", new String[]{"1291_9", "PC-E1", "2014-02-03", "2014-02-18", "Begin Landing Your Troops & The Dark Path"}),
            Map.entry("2291_2009", new String[]{"1291_9", "PC-E2", "2014-02-18", "2014-05-02", "Begin Landing Your Troops & The Dark Path"}),
            Map.entry("2291_1019", new String[]{"1291_19", "", "2014-01-10", "2014-02-03", null}),
            Map.entry("2291_2019", new String[]{"1291_19", "PC-E1", "2014-02-03", "2014-02-18", null}),
            Map.entry("2291_3019", new String[]{"1291_19", "PC-E2", "2014-02-18", "2014-07-09", null}),
            Map.entry("2273_10", new String[]{"1273_10", "PC-E1", "2011-07-14", "2011-07-25", null}),
            Map.entry("2273_1010", new String[]{"1273_10", "PC-E3", "2012-07-21", "2013-02-25", null}),
            Map.entry("2273_2010", new String[]{"1273_10", "PC-E4", "2013-02-25", "2013-04-27", null}),
            Map.entry("2281_1032", new String[]{"1281_32", "PC-E1", "2012-07-13", "2012-07-18", null}),
            Map.entry("2281_2032", new String[]{"1281_32", "PC-E3", "2012-07-21", "2013-04-27", null}),
            Map.entry("2281_32", new String[]{"1281_32", "PC-E4", "2013-04-27", "2013-05-07", null}),
            Map.entry("2281_1037", new String[]{"1281_37", "PC-E1", "2012-07-13", "2012-07-18", null}),
            Map.entry("2281_2037", new String[]{"1281_37", "PC-E3", "2012-07-21", "2013-04-27", null}),
            Map.entry("2281_37", new String[]{"1281_37", "PC-E4", "2013-04-27", "2013-05-07", null}),
            Map.entry("2220_36", new String[]{"1220_36", "", "2009-12-07", "2012-07-21", null}),
            Map.entry("2220_1036", new String[]{"1220_36", "PC-E1", "2012-07-21", "2013-02-25", null}),
            Map.entry("2220_3036", new String[]{"1220_36", "PC-E2", "2013-02-25", "2013-10-09", null}),
            Map.entry("2220_2036", new String[]{"1220_36", "PC-E3", "2013-10-09", "2014-02-03", null}),
            Map.entry("2261_1024", new String[]{"1261_24", "", "2009-09-04", "2010-04-20", null}),
            Map.entry("2261_24", new String[]{"1261_24", "PC-E1", "2010-04-20", "2012-07-21", null}),
            Map.entry("2261_2024", new String[]{"1261_24", "PC-E2", "2012-07-21", "2013-02-25", null}),
            Map.entry("2261_3024", new String[]{"1261_24", "PC-E3", "2013-02-25", "2013-04-27", null}),
            Map.entry("2261_21", new String[]{"1261_21", "", "2009-09-04", "2012-07-21", null}),
            Map.entry("2261_1021", new String[]{"1261_21", "PC-E1", "2012-07-21", "2013-02-25", null}),
            Map.entry("2261_2021", new String[]{"1261_21", "PC-E2", "2013-02-25", "2013-04-27", null}),
            Map.entry("2261_93", new String[]{"1261_93", "", "2009-09-04", "2011-07-14", "Krayt Dragon Howl & Armed And Dangerous"}),
            Map.entry("2261_1093", new String[]{"1261_93", "PC-E1", "2011-07-14", "2012-07-21", null}),
            Map.entry("2261_2093", new String[]{"1261_93", "PC-E3", "2013-02-25", "2013-04-27", null}),
            Map.entry("2273_3010", new String[]{"1273_10", "PC-E2", "2011-07-25", "2012-07-21", null}),
            Map.entry("2281_3032", new String[]{"1281_32", "PC-E2", "2012-07-18", "2012-07-21", null}),
            Map.entry("2281_3037", new String[]{"1281_37", "PC-E2", "2012-07-18", "2012-07-21", null}),
            Map.entry("2210_3136", new String[]{"1210_136", "PC-E1", "2012-07-21", "2013-02-25", null}),
            Map.entry("2282_3023", new String[]{"1282_23", "PC-E1", "2012-12-12", "2013-04-27", "Found Someone You Have & Higher Ground"}),
            Map.entry("2261_3093", new String[]{"1261_93", "PC-E2", "2012-07-21", "2013-02-25", null}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(71, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, LocalDate.parse(v[2]), version.printedOn());
            assertEquals(id, LocalDate.parse(v[3]), version.supersededOn());
            assertEquals(id, v[4], card.getPrintedTitle());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            assertNull(v[0], current.getLegacyVersion());
            assertEquals(id, current.getTitle(), card.getTitle());
            assertEquals(id, current.getUniqueness(), card.getUniqueness());
        });
    }

    @Test
    public void RepeatedTextsAreSeparateVersions() {
        // Card-list repeats of an earlier wording are their own versions (RULINGS.md 2026-11-11).
        assertEquals(library.getSwccgoCardBlueprint("2261_2033").getGameText(), library.getSwccgoCardBlueprint("2261_4033").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("1261_33").getGameText(), library.getSwccgoCardBlueprint("2261_5033").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("1220_169").getGameText(), library.getSwccgoCardBlueprint("2220_4169").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("2263_26").getGameText(), library.getSwccgoCardBlueprint("2263_2026").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("2220_36").getGameText(), library.getSwccgoCardBlueprint("2220_3036").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("1261_33").getGameText(), library.getSwccgoCardBlueprint("2261_1033").getGameText());
        assertTrue(library.getSwccgoCardBlueprint("2261_3033").getGameText().contains("Execution Arena"));
        assertTrue(library.getSwccgoCardBlueprint("2261_2024").getGameText().contains("Subjugated Planet"));
        assertTrue(library.getSwccgoCardBlueprint("1261_24").getGameText().contains("Subjugated planet"));
        assertTrue(library.getSwccgoCardBlueprint("2261_1021").getGameText().contains("Subjugated Planet"));
        assertTrue(library.getSwccgoCardBlueprint("1261_21").getGameText().contains("Subjugated planet"));
        assertTrue(library.getSwccgoCardBlueprint("2220_2036").getGameText().contains("TIE on table from Reserve Deck"));
        assertNotEquals(library.getSwccgoCardBlueprint("1220_36").getGameText(), library.getSwccgoCardBlueprint("2220_2036").getGameText());
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertTrue(library.getSwccgoCardBlueprint("1263_26").getGameText().contains("[Virtual Block 4]"));
        assertFalse(library.getSwccgoCardBlueprint("2263_26").getGameText().contains("[Virtual Block 4]"));
        assertFalse(library.getSwccgoCardBlueprint("1210_73").hasIcon(Icon.INTERIOR_SITE));
        assertTrue(library.getSwccgoCardBlueprint("2210_73").hasIcon(Icon.INTERIOR_SITE));
        assertFalse(library.getSwccgoCardBlueprint("1240_55").hasIcon(Icon.EPISODE_I));
        assertTrue(library.getSwccgoCardBlueprint("2240_55").hasIcon(Icon.EPISODE_I));
        assertFalse(library.getSwccgoCardBlueprint("2240_1055").hasIcon(Icon.EPISODE_I));
        assertTrue(library.getSwccgoCardBlueprint("2240_2055").hasIcon(Icon.EPISODE_I));
        // Scoundrel's Luck: the PC card image (2013-05-07) is the latest version; no "(even if inactive)"
        assertFalse(library.getSwccgoCardBlueprint("1281_37").getGameText().contains("(even if inactive)"));
        assertTrue(library.getSwccgoCardBlueprint("2281_37").getGameText().contains("(even if inactive)"));
        assertTrue(library.getSwccgoCardBlueprint("2281_1037").getGameText().contains("(even if inactive)"));
        assertTrue(library.getSwccgoCardBlueprint("2210_2073").hasIcon(Icon.INTERIOR_SITE));
        assertTrue(library.getSwccgoCardBlueprint("2291_71").getGameText().contains("Cannnon"));
        assertEquals("A Step Backward & Higher Ground", library.getSwccgoCardBlueprint("1282_23").getTitle());
        assertEquals("Begin Landing Your Troops & Dark Forces", library.getSwccgoCardBlueprint("1291_9").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("1291_19").getGameText().contains("they use their game text"));
        assertTrue(library.getSwccgoCardBlueprint("1291_69_BACK").getGameText().contains("(limit 3)"));
        assertFalse(library.getSwccgoCardBlueprint("2291_69_BACK").getGameText().contains("(limit 3)"));
        assertEquals(library.getSwccgoCardBlueprint("601_101").getGameText(), library.getSwccgoCardBlueprint("1210_136").getGameText());
        assertTrue(library.getSwccgoCardBlueprint("1210_136").getGameText().contains("each player may place"));
        assertEquals(library.getSwccgoCardBlueprint("601_111").getGameText(), library.getSwccgoCardBlueprint("1281_32").getGameText());
    }
}
