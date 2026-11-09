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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B27 (Virtual Block 7, single-version cards with an open defect, 10 families; ai-team-skills
 * shared/gemp-legacy-cards/versions/batches.json, record legacy-versions-v4.json): The Camp (V), 5D6-RA-7 (Fivedesix) (V),
 * Admiral Pellaeon and Derek 'Hobbie' Klivian (V) have a 2012 PC card-list text (PC-E1, the current card) and their
 * release-sheet text becomes its own 22xx card. Rogue Squadron Tactics ("take non-") and Center Of Tyranny ("Declaration
 * of Rebellion") get the printed wording; Admiral Pellaeon gains the [Reflections II] icon printed on his slip.
 * Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB27VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1272_24", "Deploy on table. Once per game, may take non-[Virtual Block 1] Alderaan or Dressel, and/or a [Death Star II] Rogue Squadron pilot (except Tycho) into hand from Reserve Deck; reshuffle. Once per turn, if you just Force drained at a battleground system, may draw top card of Reserve Deck. (Immune to Alter.)"),
            Map.entry("1272_26", "Unless your [Reflections 2] objective on table, deploy on table. If [Permanent Weapon] Maul in battle at a site, and you have no unique (•) characters of ability > 3 (except Luke of ability = 4) there, lightsabers there may not be 'swung' twice. Once per game, may take one K'lor'slug or a [Setup] card into hand from Reserve Deck; reshuffle. (Immune to Alter.)"),
            Map.entry("601_178", "Adds 2 to the power of anything he pilots. Power +1 while with your leader. If at a scomp link when opponent draws a destiny of: 1-3, you may activate one force; 4-6, you may draw top card of your reserve deck; 0 or 7 you may retrieve 1 force."),
            Map.entry("1273_2", "Adds 2 to power of anything he pilots. May be reveled from hand to take Justifier into hand from reserve deck; reshuffle and deploy both simultaneously (for -1 force each). Opponent's passengers here are forfeit = 0. Once per game, if aboard Justifier may lose one force to move away from battle as a react (at hyperspeed +2)."),
            Map.entry("601_222", "Adds 2 to power of anything he pilots. While piloting Rogue 4 (or any X-Wing), draws one battle destiny if unable to otherwise. Permanent weapon is Rogue Blaster (may target a character or creature for free; draw destiny; target hit and it's forfeit = 0, if destiny + 1 > defense value)."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2272_26", new String[]{"1272_26", "", "Unless your [Reflections 2] objective on table, deploy on table. If [Permanent Weapon] Maul in battle at a site, and you have no unique (•) character of ability > 3 (except Luke of ability = 4) there, lightsabers there may not be 'swung' twice. Once per game, may take one K'lor'slug or a [Setup] card into hand from Reserve Deck; reshuffle. (Immune to Alter.)"}),
            Map.entry("2273_1", new String[]{"1273_1", "", "[Pilot] 2. Power +1 while with your leader. If at a Scomp link when opponent draws destiny of: 1-3, you may activate 1 Force; 4-6, you may draw top card of your Reserve Deck; 0 or 7, you may retrieve 1 Force."}),
            Map.entry("2273_2", new String[]{"1273_2", "", "[Pilot] 2. May be revealed from hand to take Justifier into hand from Reserve Deck; reshuffle and deploy both simultaneously (for -1 Force each). Opponent's passengers here are forfeit = 0. Once per game, if aboard Justifier, may lose 1 Force to move away from battle as a 'react' (at hyperspeed +2)."}),
            Map.entry("2273_24", new String[]{"1273_24", "", "[Pilot] 2. While piloting Rogue 4 (or any X-wing), draws one battle destiny if unable to otherwise. Permanent weapon is Rogue Blaster (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value)."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(5, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(4, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, LocalDate.of(2012, 7, 21), version.supersededOn());
            assertEquals(id, card.getTitle(), library.getSwccgoCardBlueprint(v[0]).getTitle());
        });
        assertEquals(LocalDate.of(2010, 12, 15), library.getSwccgoCardBlueprint("2272_26").getLegacyVersion().printedOn());
        for (String id : new String[]{"2273_1", "2273_2", "2273_24"})
            assertEquals(id, LocalDate.of(2011, 6, 3), library.getSwccgoCardBlueprint(id).getLegacyVersion().printedOn());
    }

    @Test
    public void AliasedPrintingsUseTheCurrentClass() {
        assertEquals(library.getSwccgoCardBlueprint("601_178").getGameText(), library.getSwccgoCardBlueprint("1273_1").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("601_222").getGameText(), library.getSwccgoCardBlueprint("1273_24").getGameText());
    }

    @Test
    public void AdmiralPellaeonHasTheReflectionsIIIconAsPrinted() {
        for (String id : new String[]{"1273_2", "2273_2"}) {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertTrue(id, card.hasIcon(Icon.PILOT));
            assertTrue(id, card.hasIcon(Icon.REFLECTIONS_II));
        }
    }

    @Test
    public void CenterOfTyrannyPrintsDeclarationOfRebellionAsOnTheSlip() {
        assertTrue(library.getSwccgoCardBlueprint("1273_42").getGameText().contains("'1' on Declaration of Rebellion is treated as '2'"));
    }
}
