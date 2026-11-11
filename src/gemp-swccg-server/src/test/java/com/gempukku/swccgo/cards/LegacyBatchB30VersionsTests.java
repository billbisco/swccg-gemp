package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B30 (title-change versions, Virtual Blocks 4-7; ai-team-skills shared/gemp-legacy-cards/versions/
 * BATCH-METHOD.md). Bill (2026-11-09): a title change is a version (Ice-Heart, Thrawn's Ysalamir, Infinity "and every
 * similar situation"). Each original-title printing is its own 22xx card with the printed title (display only), the
 * release-sheet text and lore, and the same title/persona for every rule; the current cards keep the Master List
 * title and text. Printed titles are also pinned in LegacyVersionPrintedTitleTests, images in cardImageUrlTests.js.
 */
public class LegacyBatchB30VersionsTests {
    private static SwccgCardBlueprintLibrary library;

    /** version id -> {current id, printed title, printed-on date} */
    private static final Map<String, Object[]> VERSIONS = new LinkedHashMap<>() {{
        put("2240_17", new Object[]{"1240_17", "Thrawn's Pet", LocalDate.of(2009, 12, 7)});
        put("2240_66", new Object[]{"1240_66", "Fallen Jedi", LocalDate.of(2009, 12, 7)});
        put("2240_112", new Object[]{"1240_112", "BoShek's Modified Light Freighter", LocalDate.of(2009, 12, 7)});
        put("2250_8", new Object[]{"1250_8", "The Mandalorian", LocalDate.of(2009, 12, 7)});
        put("2263_1", new Object[]{"1263_1", "Bane, The Bounty Hunter", LocalDate.of(2010, 3, 12)});
        put("2263_7", new Object[]{"1263_7", "The Emperor's Reach", LocalDate.of(2010, 3, 12)});
        put("2272_15", new Object[]{"1272_15", "Blind Jedi", LocalDate.of(2010, 12, 15)});
        put("2272_18", new Object[]{"1272_18", "Kashyyyk Insurgent Leader", LocalDate.of(2010, 12, 15)});
    }};

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void EachOriginalTitleIsItsOwnVersionOfTheCurrentCard() {
        for (var e : VERSIONS.entrySet()) {
            String id = e.getKey();
            String currentId = (String) e.getValue()[0];
            assertEquals(id, id, library.getBaseBlueprintId(id));
            SwccgCardBlueprint version = library.getSwccgoCardBlueprint(id);
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(currentId);
            assertNotNull(id, version);
            assertNull(currentId, current.getLegacyVersion());
            assertNull(currentId, current.getPrintedTitle());
            LegacyVersion lv = version.getLegacyVersion();
            assertNotNull(id, lv);
            assertEquals(id, currentId, lv.currentPrintingId());
            assertEquals(id + ": the original printing has no label", "", lv.label());
            assertEquals(id, e.getValue()[2], lv.printedOn());
            // 2272_18: the rename took effect on 2013-01-29 (PC "New Persona Changes"); dated so in batch B39, before the rescinded 2013-04-13 errata (2272_1018)
            assertEquals(id, id.equals("2272_18") ? LocalDate.of(2013, 1, 29) : LocalDate.of(2013, 4, 27), lv.supersededOn());
            assertEquals(id, e.getValue()[1], version.getPrintedTitle());
            assertEquals(id, e.getValue()[1], GameUtils.getFullName(version));
            // same card for every rule
            assertEquals(id, current.getTitle(), version.getTitle());
            assertEquals(id, current.getUniqueness(), version.getUniqueness());
            assertEquals(id, current.getSide(), version.getSide());
            assertEquals(id, current.getPersonas(), version.getPersonas());
            assertEquals(id, current.getDeployCost(), version.getDeployCost());
            assertEquals(id, current.getForfeit(), version.getForfeit());
            assertTrue(id, LegacyCardPool.implementedVersionIds(library).contains(id));
        }
    }

    @Test
    public void GameTextIsUnchangedExceptTheEmperorsReachComma() {
        for (String id : VERSIONS.keySet()) {
            if (id.equals("2263_7"))
                continue;
            SwccgCardBlueprint version = library.getSwccgoCardBlueprint(id);
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint((String) VERSIONS.get(id)[0]);
            assertEquals(id, current.getGameText(), version.getGameText());
        }
        assertEquals("[Pilot] 3. Once per game, may [upload] a TIE Defender. While piloting, opponent may not cancel or substitute battle destiny draws here. While piloting an Imperial starfighter, it is maneuver +2 and he draws one battle destiny if unable to otherwise.",
                library.getSwccgoCardBlueprint("2263_7").getGameText());
        assertEquals("[Pilot] 3. Once per game may [upload] a TIE Defender. While piloting, opponent may not cancel or substitute battle destiny draws here. While piloting an Imperial starfighter, it is maneuver +2 and he draws one battle destiny if unable to otherwise.",
                library.getSwccgoCardBlueprint("1263_7").getGameText());
    }

    @Test
    public void LoreAsPrintedOnEachSlip() {
        assertEquals("With only a few Jedi remaining in the galaxy, each confrontation between them has far reaching consequences. Female.",
                library.getSwccgoCardBlueprint("2240_66").getLore());
        assertEquals("", library.getSwccgoCardBlueprint("1240_66").getLore());
        assertEquals("Modern Corellian design. Length 100 meters. Features engine system similar to that of a Corellian corvette. Dorsal hatch reveals hangar bay.",
                library.getSwccgoCardBlueprint("2240_112").getLore());
        assertEquals("", library.getSwccgoCardBlueprint("2263_1").getLore());
        assertEquals("Bounty hunter.", library.getSwccgoCardBlueprint("1263_1").getLore());
        assertEquals("Wookiee. \"GHRRRRAARRRRHG!\"", library.getSwccgoCardBlueprint("2272_18").getLore());
        assertEquals("Leader.", library.getSwccgoCardBlueprint("1272_18").getLore());
        assertEquals("Leader.", library.getSwccgoCardBlueprint("1263_7").getLore());
    }

    @Test
    public void CharacteristicsInTheOldTitleAreKept() {
        // "Bane, The Bounty Hunter" and "Kashyyyk Insurgent Leader": the PC kept every card's characteristics with the
        // 2013 persona changes (the new lore carries the word the old title had)
        assertTrue(library.getSwccgoCardBlueprint("2263_1").hasKeyword(Keyword.BOUNTY_HUNTER));
        assertTrue(library.getSwccgoCardBlueprint("2272_18").hasKeyword(Keyword.LEADER));
        assertTrue(library.getSwccgoCardBlueprint("2240_66").hasKeyword(Keyword.FEMALE));
    }

    @Test
    public void TarffulCurrentTextIsTheMasterListText() {
        assertEquals("\"Occupy\" on Forest Depths is treated as \"control.\" Non-unique Wookiees here are Heroic. Once per game, when deployed, may 'roar' to take one card into hand from Used Pile (reshuffle) and/or stack a Wookiee from hand on Grrrghrrrgh!.",
                library.getSwccgoCardBlueprint("1272_18").getGameText());
        assertNotEquals(library.getSwccgoCardBlueprint("1272_18").getClass(), library.getSwccgoCardBlueprint("2272_18").getClass());
    }
}
