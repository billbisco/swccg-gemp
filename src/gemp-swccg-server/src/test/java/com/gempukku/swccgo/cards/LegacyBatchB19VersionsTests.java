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

/**
 * Legacy batch B19 (Virtual Blocks 7-8, 11 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest text and each older version is its own 22xx card with its text, version label,
 * current printing and dates. Images are pinned in cardImageUrlTests.js. 601_17 is the current class of 1281_5.
 * Tibanna Floating Refinery and Grievous' Lightsabers were retitled with their errata (2274_4 / 2274_11 keep the
 * current title in GEMP). Coruscant: Main Power Plant has one known text (its first printing is lost, held for Bill).
 */
public class LegacyBatchB19VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1281_1", "Once per game, deploy a device on Aurra from Reserve Deck; reshuffle. While armed with a [Reflections III] rifle, it may not be retargeted or stolen, her total weapon destiny is +1, and characters she hits may not be used to satisfy attrition. Immune to attrition < 3 (or < 4 if armed with a rifle)."),
            Map.entry("1281_4", "Adds 2 to the power of anything he pilots. May not be targeted by weapons unless your other slavers present are each 'hit'. Permanent weapon is •Bossk's Mortar Gun (may target a character for free. Draw destiny. Target hit and forfeit =0, if destiny matches target's destiny number)."),
            Map.entry("601_17", "Adds 2 to power of anything he pilots. While with opponent's character of ability > 2, Ket Maliss is power +1 and defense value + 2 and draws one battle destiny if unable to otherwise. Whenever opponent's character was lost from same site, opponent loses 1 Force."),
            Map.entry("1273_36", "If [Virtual Block 7] Rebel Strike Team on table, deploy on table. Your [Endor] Epic Event total is +1 for each of your non-Jedi scouts at Bunker. For each of your non-Jedi scouts in battle, attrition against opponent is +1. While opponent's [Endor] objective on table, Bunker may not be converted. Once per turn may take Bunker into hand from Reserve Deck; reshuffle. (Immune to Alter.)"),
            Map.entry("1274_18", "Unless Imperial Atrocity on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 6 (ignore other modifiers); opponent may take any one card into hand from Reserve Deck; reshuffle. Immune to Control. OR Use 2 Force to deploy on table and stack one just-played Interrupt here."),
            Map.entry("1274_4", "Deploy on a cloud sector. Your Force generation is +1 at cloud sectors you occupy. Once during your control phase, if you occupy this sector and a battleground site, may draw destiny. If destiny > 2, retrieve 1 Force. If lost from table, place this Device in Used Pile."),
            Map.entry("1274_11", "Deploy on Grievous. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and may not satisfy attrition (or be removed from Lost Pile this turn), if total destiny > defense value."));
    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2281_1", new String[]{"1281_1", "", "Once per game may deploy a device on Aurra from Reserve Deck; reshuffle. While armed with a [Reflections III] rifle: it may not be retargeted or stolen, her total weapon destiny is +1, and characters she hits may not be used to satisfy attrition. Immune to attrition < 3 (< 4 while armed with a rifle)."}),
            Map.entry("2281_4", new String[]{"1281_4", "", "Adds 2 to the power of anything he pilots. May not be targeted by weapons unless your other slavers present are each 'hit.' Permanent weapon is •Bossk's Mortar Gun (may target a character for free; draw destiny; target hit, and its forfeit = 0, if destiny = target's destiny number)."}),
            Map.entry("2281_5", new String[]{"1281_5", "", "Adds 2 to power of anything he pilots. While with opponent's character of ability > 2, Ket Maliss is power +1, defense value +2, and draws one battle destiny if unable to otherwise. Whenever an opponent's character is lost from same site, opponent loses 1 Force."}),
            Map.entry("2273_36", new String[]{"1273_36", "", "If [Virtual Block 7] Rebel Strike Team on table, deploy on table. Your [Endor] Epic Event total is +1 for each non-Jedi scout at Bunker. For each of your non-Jedi scouts in battle, attrition against opponent is +1. While opponent's [Endor] objective on table, Bunker may not be converted. May take Bunker into hand from Reserve Deck; reshuffle. (Immune to Alter.)"}),
            Map.entry("2273_1036", new String[]{"1273_36", "PC-E1", "If [Virtual Block 7] Rebel Strike Team on table, deploy on table. Your [Endor] Epic Event total is +1 for each non-Jedi scout at Bunker. For each of your non-Jedi scouts in battle, attrition against opponent is +1. While opponent's [Endor] objective on table, Bunker may not be converted. Once per turn may take Bunker into hand from Reserve Deck; reshuffle. (Immune to Alter.)"}),
            Map.entry("2274_18", new String[]{"1274_18", "", "Unless Imperial Atrocity on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may take any one card into hand from Reserve Deck; reshuffle. Immune to Control. OR Use 2 Force to deploy on table and stack one just-played Interrupt here."}),
            Map.entry("2274_4", new String[]{"1274_4", "", "Deploy on a cloud sector (limit one per sector). Your Force generation is +1 at cloud sectors you occupy. Once during your control phase, if you occupy this sector, may draw destiny. If destiny > 2, retrieve 1 Force. If lost from table, place this Device in Used Pile."}),
            Map.entry("2274_11", new String[]{"1274_11", "", "Deploy on Cyborg Commander. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and may not satisfy attrition (or be removed from Lost Pile this turn), if total destiny > defense value."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(7, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(8, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
        });
    }

    @Test
    public void DatesFollowTheRecord() {
        // PC website card list by 2012-07-21 (earliest capture with text): Shield Is Down PC-E1, Hindsight PC-E1, the 8.1 rephrases
        for (String id : new String[]{"2281_1", "2281_4", "2281_5"})
            assertEquals(id, LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint(id).getLegacyVersion().supersededOn());
        // Shield Is Down PC-E1 and Hindsight PC-E1: PC forum errata of 2012-01-13 and 2012-04-11 (legal 2012-04-20) (Batch B37)
        assertEquals(LocalDate.of(2012, 1, 13), library.getSwccgoCardBlueprint("2273_36").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 4, 20), library.getSwccgoCardBlueprint("2274_18").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 1, 13), library.getSwccgoCardBlueprint("2273_1036").getLegacyVersion().printedOn());
        // Master List 2013-04-27 (Grievous persona); Shield Is Down PC-E2 and Tibanna Floating Refinery: PC forum errata
        // of 2012-10-19 and 2012-11-02 (Batch B36)
        assertEquals(LocalDate.of(2012, 10, 19), library.getSwccgoCardBlueprint("2273_1036").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 10, 30), library.getSwccgoCardBlueprint("2274_4").getLegacyVersion().supersededOn());
        for (String id : new String[]{"2273_17", "2273_20", "2274_11"})
            assertEquals(id, LocalDate.of(2013, 4, 27), library.getSwccgoCardBlueprint(id).getLegacyVersion().supersededOn());
        // Coruscant (V): Dark Force icon errata 2011-06-23 (PC-E1), AR 2.0 rephrase (PC-E2)
        assertEquals(LocalDate.of(2011, 6, 22), library.getSwccgoCardBlueprint("2273_1041").getLegacyVersion().supersededOn());
        assertEquals("PC-E1", library.getSwccgoCardBlueprint("2273_41").getLegacyVersion().label());
    }

    @Test
    public void TextsAndLoreAsPrinted() {
        assertEquals("Force drain -1 here . Jedi here may not have their power reduced.", library.getSwccgoCardBlueprint("2273_17").getLocationLightSideGameText());
        assertEquals("While Insidious Prisoner on table, Grievous is immune to attrition.", library.getSwccgoCardBlueprint("1273_17").getLocationDarkSideGameText());
        assertEquals("Highly valued by Jabba the Hutt and other gangsters. Ket Maliss, Prince Xizor's \u201cshadow killer,\u201d has unknown but undoubtedly lethal business in Mos Eisley. Assassin.", library.getSwccgoCardBlueprint("601_17").getLore());
        assertEquals(library.getSwccgoCardBlueprint("601_17").getLore(), library.getSwccgoCardBlueprint("2281_5").getLore());
        assertEquals("\"I'm backwards! You fleabitten furball...only an overgrown mophead like you would be stupid enough\u2014\"", library.getSwccgoCardBlueprint("1274_18").getLore());
        assertEquals(library.getSwccgoCardBlueprint("1274_18").getLore(), library.getSwccgoCardBlueprint("2274_18").getLore());
        assertEquals("Trandoshan assassin and slaver.", library.getSwccgoCardBlueprint("1281_4").getLore());
    }
}
