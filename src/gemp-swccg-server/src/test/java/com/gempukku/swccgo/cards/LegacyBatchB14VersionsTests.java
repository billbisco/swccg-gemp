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
 * Legacy batch B14 (Virtual Blocks 2-4, 11 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest text and each older version is its own 22xx card with its text, version label,
 * current printing and dates. Images (CardMaker recreations of each latest text that no published image shows) are
 * pinned in cardImageUrlTests.js. 601_237 / 601_89 / 601_81 / 601_88 are the current classes of 1220_128 / 1240_2 /
 * 1240_8 / 1240_27. Galen Marek, Starkiller (AI) (2240_8ai) is held for Bill: no pre-rename AI printing is known.
 */
public class LegacyBatchB14VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("601_237", "Deploy on table. During your deploy phase, may reveal an unpiloted combat vehicle from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. May not be suspended. (Immune to Alter.)"),
            Map.entry("1220_136", "Deploy on table. During battles where your Jedi is present, players may not draw more than two battle destiny. Once during your turn, may take Inner Strength into hand from Reserve Deck; reshuffle. (Immune to Alter.)"),
            Map.entry("1220_163", "Deploy Hoth system and [Virtual Block 6] Main Power Generators. May deploy Echo Base Garrison and/or Maneuvering Flaps. For remainder of game, you may not deploy Hoth Sentry or non-Hoth locations (except Rendezvous Point). T-47 Battle Formation may only be played once per game and is immune to Sense. While this side up, during your deploy phase, may deploy an exterior Hoth site or a non-unique Rebel to Hoth from Reserve Deck; reshuffle. Flip this card during your move phase if you control three exterior Hoth sites with T-47s and/or non-unique Rebels. Place out of play if Main Power Generators 'blown away.'"),
            Map.entry("1230_13", "Adds 3 to power of anything he pilots. May steal (and then use) an opponent's lightsaber just lost from same site; may then use two weapons. While The Quick And Easy Path on table, Jedi Escape and What Was It? may not cancel duels. Immune to attrition < 5."),
            Map.entry("1230_77", "Once per game, may take a rifle and/or an Interrupt with 'scout' in game text into hand from Reserve Deck; reshuffle. While alone at an exterior site, opponent's weapon destiny draws here are -2. Immune to attrition < 4."),
            Map.entry("601_89", "Adds 3 to power of anything she pilots.  Deploys -1 to starships.  Naboo Blaster is a matching weapon for her.  While with Galen at a site, adds one battle destiny.  During your control phase, may use 2 Force to retrieve a card with 'back' in title.  Immune to attrition < 3."),
            Map.entry("1240_4", "Imperial Council Member. Agendas: logistics. ISB Agents are forfeit +1. While at Conference Room with Sim Aloo, may use 1 Force to subtract 1 from an opponent's just drawn non-Epic Event destiny."),
            Map.entry("601_81", "Galen's forfeit may not be reduced. Power and defense value +2 while with Maris Brood. If defending a battle alone (or defending with Juno Eclipse), may be targeted by Force Lightning. Immune to Clash Of Sabers and attrition < 4."),
            Map.entry("601_88", "If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose force during opponent's control phase (except from a Force drain at a battleground or your card), that loss is cumulatively -1 (to a minimum of 1). Non-[Virtual Block 4] Imperial Propaganda is canceled."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2220_128", new String[]{"1220_128", "", "Deploy on table. During your deploy phase, may reveal an unpiloted combat vehicle from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. (Immune to Alter.)"}),
            Map.entry("2220_136", new String[]{"1220_136", "", "Deploy on table. During battles where your Jedi is present, players may not draw more than two battle destiny. During your turn, may take Inner Strength into hand from Reserve Deck; reshuffle. (Immune to Alter.)"}),
            Map.entry("2220_163", new String[]{"1220_163", "", "Deploy Hoth system and [Virtual Block 6] Main Power Generators. May deploy Echo Base Garrison and/or Maneuvering Flaps. For remainder of game, you may not deploy Hoth Sentry or non-Hoth locations (except Rendezvous Point). T-47 Battle Formation is an Out Of Play Interrupt and immune to Sense. While this side up, during your deploy phase, may deploy an exterior Hoth site or a non-unique Rebel to Hoth from Reserve Deck; reshuffle. Flip this card during your move phase if you control three exterior Hoth sites with T-47s and/or non-unique Rebels. Place out of play if Main Power Generators 'blown away.'"}),
            Map.entry("2230_13", new String[]{"1230_13", "", "Adds 3 to power of anything he pilots. May deploy an opponent's lightsaber just lost from same site on him (for free); may then use two weapons. While The Quick And Easy Path on table, Jedi Escape and What Was It? may not cancel duels. Immune to attrition < 5."}),
            Map.entry("2230_77", new String[]{"1230_77", "", "Once per game, may take a rifle and/or an Interrupt with 'scout' in game text into hand from Reserve Deck; reshuffle. Unless Undercover, while alone at an exterior site, opponent's weapon destiny draws here are -2. Immune to attrition < 4."}),
            Map.entry("2240_2", new String[]{"1240_2", "", "Adds 3 to power of anything she pilots.  Deploys -1 to starships.  Naboo Blaster is a matching weapon for Black Leader.  While with Galen at a site, adds one battle destiny.  During your control phase, may use 2 Force to retrieve a card with 'back' in title.  Immune to attrition < 3."}),
            Map.entry("2240_3", new String[]{"1240_3", "", "Adds 3 to power of anything she pilots.  Deploys -1 to starships.  Naboo Blaster is a matching weapon for Black Leader.  While with Galen at a site, adds one battle destiny.  During your control phase, may use 2 Force to retrieve a card with 'back' in title.  Immune to attrition < 3."}),
            Map.entry("2240_4", new String[]{"1240_4", "", "Imperial Council Member. Agenda: logistics. ISB Agents are forfeit +1. While at Conference Room with Sim Aloo, may use 1 Force to subtract 1 from an oponent's just drawn non-Epic Event destiny."}),
            Map.entry("2240_8", new String[]{"1240_8", "", "Galen's forfeit may not be reduced. Power and defense value +2 while with Fallen Jedi. If defending a battle alone (or defending with Black Leader), may be targeted by Force Lightning. Immune to Clash Of Sabers and attrition < 4."}),
            Map.entry("2240_27", new String[]{"1240_27", "", "If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose force during opponent's control phase (except from a Force drain at a battleground or your card), that loss is cumulatively -1 (to a minimum of 1)."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(9, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(10, VERSIONS.size());
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
    public void SupersededDatesFollowTheRecord() {
        // Maneuvering Flaps (V): PC-E1 shown on the PC website card list by 2012-07-21 (AR 2.0 PDF 2012-08-27)
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2220_128").getLegacyVersion().supersededOn());
        // Colonel Wullf Yularen (V): card list 2012-07-21; Orrimaarko (V): AR 2.0 (2012-08-27); the rest: Master List 2013-04-27
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2240_4").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 8, 27), library.getSwccgoCardBlueprint("2230_77").getLegacyVersion().supersededOn());
        for (String id : new String[]{"2220_136", "2220_163", "2230_13", "2240_2", "2240_3", "2240_8", "2240_27"})
            assertEquals(id, LocalDate.of(2013, 4, 27), library.getSwccgoCardBlueprint(id).getLegacyVersion().supersededOn());
    }

    @Test
    public void LoreAsPrinted() {
        // the release-sheet originals of Black Leader and Galen, Secret Apprentice had lore; the Master List removed it
        assertEquals("A shining example of Imperial honor and bravery. Female.", library.getSwccgoCardBlueprint("2240_2").getLore());
        assertEquals("A shining example of Imperial honor and bravery. Female.", library.getSwccgoCardBlueprint("2240_3").getLore());
        assertEquals("", library.getSwccgoCardBlueprint("601_89").getLore());
        assertEquals("'Perhaps you are not as strong as the Emperor thought.'", library.getSwccgoCardBlueprint("2240_8").getLore());
        assertEquals("", library.getSwccgoCardBlueprint("601_81").getLore());
        // PC card images: "Dresselian scout", "fulfill Emperor's will"
        assertEquals("Dresselian scout and resistance leader. Worked tirelessly to combat the subjugation of his homeworld before Bothans brought him into contact with the Alliance.", library.getSwccgoCardBlueprint("1230_77").getLore());
        assertEquals(library.getSwccgoCardBlueprint("1230_77").getLore(), library.getSwccgoCardBlueprint("2230_77").getLore());
        assertEquals("Imperial Security Bureau (ISB) officer assigned to brief Tarkin. Also ordered to ensure absolute loyalty to the Emperor. Leader. Will stop at nothing to fulfill Emperor's will.", library.getSwccgoCardBlueprint("1240_4").getLore());
        assertEquals(library.getSwccgoCardBlueprint("1240_4").getLore(), library.getSwccgoCardBlueprint("2240_4").getLore());
    }
}
