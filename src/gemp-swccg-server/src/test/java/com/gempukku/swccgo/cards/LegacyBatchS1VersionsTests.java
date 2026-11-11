package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Batch S1 (Bill 2026-11-11 07:34: every published text is a version, even a word-for-word repeat). (1) Five families:
 * Original -> PC forum errata -> PC website card list (2012) -> Master List 2013 (+ PC card image), which repeats the forum
 * words and is the current card (no play change). The PC card images of Master Qui-Gon (V) (no comma) and Master Kenobi
 * (capital "Once") differ from the Master List, so each is a version of its own, placed by its server date (2013-02-25).
 * (2) H1: Away Put Your Weapon (V), Let's Go Left (V), Kashyyyk (V), Aratech Corporation (V): the September 2009 first
 * printing is lost; the PC card image wording is the 9 September 2009 errata (PC-E1, new 22xx), the 7 December 2009
 * release sheet text PC-E2, and the card-list wording (Away Put Your Weapon, Let's Go Left) PC-E3, the current card.
 * Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchS1VersionsTests {
    /** version id -> {current printing id, label, printed on, superseded on} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2271_2002", new String[]{"1271_2", "PC-E1", "2011-03-21", "2012-07-21"}),
            Map.entry("2271_1002", new String[]{"1271_2", "PC-E2", "2012-07-21", "2013-02-25"}),
            Map.entry("2271_3002", new String[]{"1271_2", "PC-E3", "2013-02-25", "2013-04-27"}),
            Map.entry("2250_2044", new String[]{"1250_44", "PC-E1", "2011-03-21", "2012-07-21"}),
            Map.entry("2250_1044", new String[]{"1250_44", "PC-E2", "2012-07-21", "2013-02-25"}),
            Map.entry("2250_3044", new String[]{"1250_44", "PC-E3", "2013-02-25", "2013-04-27"}),
            Map.entry("2273_3027", new String[]{"1273_27", "PC-E2", "2012-04-20", "2012-07-21"}),
            Map.entry("2273_2027", new String[]{"1273_27", "PC-E3", "2012-07-21", "2013-02-25"}),
            Map.entry("2273_4027", new String[]{"1273_27", "PC-E4", "2013-02-25", "2013-04-27"}),
            Map.entry("2271_2039", new String[]{"1271_39", "PC-E1", "2012-01-19", "2012-07-21"}),
            Map.entry("2271_1039", new String[]{"1271_39", "PC-E2", "2012-07-21", "2013-02-25"}),
            Map.entry("2271_3039", new String[]{"1271_39", "PC-E3", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_2065", new String[]{"1261_65", "PC-E1", "2012-02-21", "2012-07-21"}),
            Map.entry("2261_1065", new String[]{"1261_65", "PC-E2", "2012-07-21", "2013-02-25"}),
            Map.entry("2261_3065", new String[]{"1261_65", "PC-E3", "2013-02-25", "2013-04-27"}),
            Map.entry("2220_1139", new String[]{"1220_139", "PC-E1", "2009-09-09", "2009-12-07"}),
            Map.entry("2220_139", new String[]{"1220_139", "PC-E2", "2009-12-07", "2012-07-21"}),
            Map.entry("2250_1033", new String[]{"1250_33", "PC-E1", "2009-09-09", "2009-12-07"}),
            Map.entry("2250_33", new String[]{"1250_33", "PC-E2", "2009-12-07", "2012-07-21"}),
            Map.entry("2250_76", new String[]{"1250_76", "PC-E1", "2009-09-09", "2009-12-07"}),
            Map.entry("2230_23", new String[]{"1230_23", "PC-E1", "2009-09-09", "2009-12-07"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void VersionsAreInDateOrder() {
        assertEquals(21, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, LocalDate.parse(v[2]), version.printedOn());
            assertEquals(id, LocalDate.parse(v[3]), version.supersededOn());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            assertNull(id, current.getLegacyVersion());
            assertEquals(id, current.getTitle(), card.getTitle());
        });
    }

    @Test
    public void ForumVersionsRepeatTheCurrentWords() {
        for (String[] p : new String[][]{{"2271_2002", "1271_2"}, {"2271_3002", "1271_2"}, {"2271_3039", "1271_39"}, {"2261_3065", "1261_65"}, {"2250_2044", "1250_44"}, {"2273_3027", "1273_27"}, {"2271_2039", "1271_39"}, {"2261_2065", "1261_65"}})
            assertEquals(p[0], library.getSwccgoCardBlueprint(p[1]).getGameText(), library.getSwccgoCardBlueprint(p[0]).getGameText());
        assertTrue(library.getSwccgoCardBlueprint("2250_3044").getGameText().endsWith("Immune to You Are Beaten and attrition."));
        assertTrue(library.getSwccgoCardBlueprint("2273_4027").getGameText().contains("Used Pile; Once per turn"));
    }

    @Test
    public void CurrentCardsKeepTheForumText() {
        assertTrue(library.getSwccgoCardBlueprint("1271_2").getGameText().endsWith("May not be Disarmed. Immune to Clash Of Sabers and attrition < 5."));
        assertTrue(library.getSwccgoCardBlueprint("1271_39").getGameText().contains("an Epic Event at the start"));
        assertTrue(library.getSwccgoCardBlueprint("1261_65").getGameText().contains("While piloting a freighter"));
        assertTrue(library.getSwccgoCardBlueprint("1273_27").getGameText().contains("once per turn, may lose 1 Force"));
        assertTrue(!library.getSwccgoCardBlueprint("1273_27").getGameText().contains("once per turn, once per turn"));
        assertTrue(library.getSwccgoCardBlueprint("1250_44").getGameText().endsWith("May not be Disarmed. Immune to You Are Beaten, and attrition."));
    }

    @Test
    public void H1ImageWordings() {
        assertTrue(library.getSwccgoCardBlueprint("2220_1139").getGameText().contains("retrieve up to 5 weapons (may take one retrieved card into hand)"));
        assertTrue(library.getSwccgoCardBlueprint("1220_139").getGameText().contains("retireved"));
        assertTrue(library.getSwccgoCardBlueprint("2250_1033").getGameText().contains("their non-[Presence] characters (and vehicles)"));
        assertEquals("Once per turn, if this was your starting location, may deploy a Kashyyyk site from Reserve Deck; reshuffle.",
                library.getSwccgoCardBlueprint("2250_76").getLocationLightSideGameText());
        assertEquals(library.getSwccgoCardBlueprint("1250_76").getLocationDarkSideGameText(),
                library.getSwccgoCardBlueprint("2250_76").getLocationDarkSideGameText());
        assertTrue(library.getSwccgoCardBlueprint("2230_23").getGameText().contains("During your deploy phase, you may reveal"));
        assertTrue(library.getSwccgoCardBlueprint("1230_23").getGameText().contains("During your deploy phase, may reveal"));
        assertEquals("2220_1139", LegacyVersion.versionIdOf("1220_139", 1));
        assertEquals("2250_1033", LegacyVersion.versionIdOf("1250_33", 1));
    }
}
