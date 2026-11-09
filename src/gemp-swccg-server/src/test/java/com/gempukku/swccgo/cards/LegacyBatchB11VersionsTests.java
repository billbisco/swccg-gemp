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
 * Legacy batch B11 (Virtual Blocks 6-7, 11 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json and
 * legacy-versions-v4.json): the current card carries the latest version's text and every older version is its own
 * 22xx card with its text, version label and current printing. Images (each version's own text) are pinned in
 * cardImageUrlTests.js.
 */
public class LegacyBatchB11VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1263_21", "Deploys -1 to same site as your MTT. AT-AT Cannon may deploy aboard, and targets it hits cannot be used to satisfy attrition. Unless alone, draws one battle destiny if not able to otherwise."),
            Map.entry("1271_2", "Adds 3 to power of anything he pilots. [Vader] cards never deploy unless Rise Of The Dark Lord on table. Once per turn, if at a site you control, may apply his ability to draw battle destiny elsewhere. May not be Disarmed. Immune to Clash Of Sabers and attrition < 5."),
            Map.entry("1271_10", "Deploy on table. Once per game, if Obi-Wan on table, may deploy Vader from Reserve Deck; reshuffle. May place this Effect in Used Pile to take a [Grabber] into hand from Reserve Deck; reshuffle. If you just attempted to take a card into hand or deploy a card from Reserve Deck, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card instead."),
            Map.entry("1271_17", "Deploy on [Vader] Vader. During your battle phase, may cancel this Effect to allow Vader to make a regular move. In order for opponent's character to move away from here using landspeed, opponent must first lose 1 Force. While defending a battle at opponent's site, Vader is power +2 and immune to attrition. (Immune to Alter.)"),
            Map.entry("1271_39", "If you have no Epic Events on table (and did not play an Epic Event at the start of the game), deploy on table. Once per game, may take a [Setup] card into hand from Reserve Deck; reshuffle. May place this Effect in Used Pile to take an Immediate Effect into hand from Reserve Deck; reshuffle. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)"),
            Map.entry("1271_40", "Deploy on Yavin 4; it is 'liberated.' During battles at sites related to other 'liberated' systems, may add 2 to power of each Rebel warrior (to a maximum of 4). Once per turn, if you control this system, may retrieve 1 Force when you Force drain at another 'liberated' system."),
            Map.entry("1271_46", "Deploy on Yavin 4. Once per turn, may deploy a battleground system or an interior Yavin 4 site from Reserve Deck; reshuffle. Whenever you Force drain at another battleground system, may draw destiny. Add 1 for each Yavin 4 site on table (2 if you occupy). If total destiny > 6, stack top card from any Reserve Deck face up there (limit one per system), and, while stacked there, system is 'liberated.' At 'liberated' systems: Force drains are +1, and at same and related locations your total power is +3 and you draw one battle destiny if not able to otherwise. If opponent Force drains at a 'liberated' system, place stacked card in owner's Used Pile."),
            Map.entry("1272_7", "Deploy on table. At non-[Episode I] sites, immunity to attrition of [Coruscant] Jedi is limited to < 5. Your game text on [Coruscant] systems is canceled. During your control phase, may say 'Coruscant?' and lose 1 Force to place a just-played Interrupt in owners Lost Pile. (Immune to Alter.)"),
            Map.entry("1273_10", "Deploy on a Kessel site. Opponent's Force drains are -1 here. Once per turn, if you are about to lose Force from Reserve Deck, may place a 'spice' card in owner's Lost Pile to make that Force satisfy 1 additional Force loss. Once per game may deploy Moruth Doole here from Reserve Deck; reshuffle."),
            Map.entry("1273_15", "Use 1 Force to traget your character of ability > 3 alone at a site. USED: During any move phase, take target into hand. LOST: If opponent has exactly one non-spy character at target's site, place target in Used Pile."),
            Map.entry("1273_23", "Adds 2 to the power of anything he pilots, 3: any corvette. While piloting a corvette it is immune to attrition <5 and, once per turn, may peek at a card in opponent's hand. Once per game, during battle (unless Vader here) may use 2 force to move ship away (for free)."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2263_21", new String[]{"1263_21", "", "Deploys -1 to same site as your MTT. AT-AT Cannon may deploy aboard, and targets it hits may not be used to satisfy attrition. Unless alone, draws one battle destiny if not able to otherwise."}),
            Map.entry("2271_2", new String[]{"1271_2", "", "Adds 3 to power of anything he pilots. [Vader] cards never deploy unless Rise Of The Dark Lord on table. Once per turn, if at a site you control, may apply his ability to draw battle destiny elsewhere. Immune to Clash Of Sabers, Disarmed, and attrition < 5."}),
            Map.entry("2271_1002", new String[]{"1271_2", "PC-E1", "Adds 3 to power of anything he pilots. [Vader] cards never deploy unless Rise Of The Dark Lord on table. Once per turn, if at a site you control, may apply his ability to draw battle destiny elsewhere. Immune to Clash Of Sabers and attrition < 5. May not be Disarmed."}),
            Map.entry("2271_10", new String[]{"1271_10", "", "Deploy on table. Once per game, if Obi-Wan on table, may [download] Vader. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [download] or [upload] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle."}),
            Map.entry("2271_17", new String[]{"1271_17", "", "Deploy on [Vader] Vader. During your battle phase, may cancel this Effect to allow Vader to make a regular move. In order for opponent's character to move away from here, opponent must first lose 1 Force. While defending a battle at opponent's site, Vader is power +2 and immune to attrition. (Immune to Alter.)"}),
            Map.entry("2271_39", new String[]{"1271_39", "", "Unless your Epic Event on table, deploy on table. Once per game, may take a [Setup] card into hand from Reserve Deck; reshuffle. May place this Effect in Used Pile to take an Immediate Effect into hand from Reserve Deck; reshuffle. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)"}),
            Map.entry("2271_1039", new String[]{"1271_39", "PC-E1", "If you have no Epic Events on table (and did not play an Epic Even at the start of the game), deploy on table. Once per game, may take a [Setup] card into hand from Reserve Deck; reshuffle. May place this Effect in Used Pile to take an Immediate Effect into hand from Reserve Deck; reshuffle. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)"}),
            Map.entry("2271_40", new String[]{"1271_40", "", "Deploy on Yavin 4 system; it is 'liberated'. At sites related to other 'liberated' systems, may add 2 to power of each Rebel warrior (to a maximum of 4). Once per turn, if you control this system, may retrieve 1 Force when you Force drain at another 'liberated' system."}),
            Map.entry("2271_1040", new String[]{"1271_40", "PC-E1", "Deploy on Yavin 4; it is 'liberated'. At sites related to other 'liberated' systems, may add 2 to power of each Rebel warrior (to a maximum of 4). Once per turn, if you control this system, may retrieve 1 Force when you Force drain at another 'liberated' system."}),
            Map.entry("2271_2040", new String[]{"1271_40", "PC-E2", "Deploy on Yavin 4; it is 'liberated.' At sites related to other 'liberated' systems, may add 2 to power of each Rebel warrior (to a maximum of 4). Once per turn, while you control this system, may retrieve 1 Force when you Force drain at another 'liberated' system."}),
            Map.entry("2271_46", new String[]{"1271_46", "PC-E1", "Deploy on Yavin 4. Once per turn, may deploy a battleground system or an interior Yavin 4 site from Reserve Deck; reshuffle. Whenever you Force drain at another battleground system, may draw destiny. Add 1 for each Yavin 4 site on table (2 if you occupy). If total destiny > 6, stack top card from either player's Reserve Deck face up there (limit one per system) to 'liberate' the system. At 'liberated' systems: Force drains are +1, and at same and related locations your total power is +3 and you draw one battle destiny if not able to otherwise. If opponent Force drains at a 'liberated' system, place stacked card in owner's Used Pile."}),
            Map.entry("2271_1046", new String[]{"1271_46", "", "Deploy on Yavin 4 system. Once per turn, may deploy a battleground system or an interior Yavin 4 site from Reserve Deck; reshuffle. Whenever you Force drain at another battleground system, may draw destiny. Add 1 for each Yavin 4 site on table (2 if you occupy). If total destiny > 6, stack top card from either player's Reserve Deck face up there (limit one per system) to 'liberate' the system. At 'liberated' systems: Force drains are +1, and at same and related locations your total power is +3 and you draw one battle destiny if not able to otherwise. If opponent Force drains at a 'liberated' system, place stacked card in owner's Used Pile."}),
            Map.entry("2271_2046", new String[]{"1271_46", "PC-E2", "Deploy on Yavin 4. Once per turn, may deploy a battleground system or an interior Yavin 4 site. Whenever you Force drain at another battleground system, may draw destiny. Add 1 for each Yavin 4 site on table (2 if you occupy). If total destiny > 6, stack top card from either player's Reserve Deck there (limit one per system) to 'liberate' the system. At 'liberated' systems: Force drains are +1, and at same and related locations your total power is +3 and you draw one battle destiny if not able to otherwise. If opponent Force drains at a 'liberated' system, place stacked card in owner's Used Pile."}),
            Map.entry("2272_7", new String[]{"1272_7", "", "Deploy on table. At non-[Episode I] sites, immunity to attrition of [Coruscant] Jedi is limited to < 5. Your gametext on [Coruscant] systems is canceled. During your control phase, may say 'Coruscant?' and lose 1 Force to place a just-resolved Interrupt in owner's Lost Pile. [Immune to Alter.]"}),
            Map.entry("2273_10", new String[]{"1273_10", "", "Deploy on a Kessel site. Opponent's Force drains are -1 here. Once per turn, if you are about to lose Force from Reserve Deck, may place a 'spice' card in owner's Lost Pile to make that Force satisfy 1 additional Force loss. Once per game, may deploy Spice Mine Administrator here from Reserve Deck; reshuffle."}),
            Map.entry("2273_1010", new String[]{"1273_10", "PC-E1", "Deploy on a Kessel site. Opponents Force drains are -1 here. Oncer per turn, if you just lost a card from Reserve Deck, may place a 'spice' card in owners Lost Pile to satisfy 1 additional Force loss. Once per game, may deploy Spice Mine Administrator here."}),
            Map.entry("2273_15", new String[]{"1273_15", "", "Use 1 Force to target your character of ability > 3 alone at a site. USED: During any move phase, take target into hand. LOST: If opponent has exactly one non-spy character at target's site, place target in Used Pile."}),
            Map.entry("2273_23", new String[]{"1273_23", "", "[Pilot] 2, 3: any corvette. While piloting a corvette, it is immune to attrition < 5 and, once per turn, may peek at a card in opponent's hand. Once per game, during battle (unless Vader here) may use 2 Force to move starship he pilots away (for free)."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(11, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(18, VERSIONS.size());
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
    public void LukeTrustMeCardListTextIsPcE1AndTheMasterListTextIsCurrent() {
        // v4: the PC website card list (by 2012-07-21) printed "Epic Even"; the Master List (2013-04-27) prints "Epic Event".
        LegacyVersion e1 = library.getSwccgoCardBlueprint("2271_1039").getLegacyVersion();
        assertEquals(LocalDate.of(2012, 7, 21), e1.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), e1.supersededOn());
        assertEquals(true, library.getSwccgoCardBlueprint("1271_39").getGameText().contains("Epic Event at the start"));
    }

    @Test
    public void EvacuateLoreAsPrinted() {
        String lore = "Escape pods are on many starships allowing those in peril to flee, an act considered cowardly by Imperial officers. 'We've analyzed their attack, sir, and there is a danger . . .'";
        assertEquals(lore, library.getSwccgoCardBlueprint("1273_15").getLore());
        assertEquals(lore, library.getSwccgoCardBlueprint("2273_15").getLore());
    }
}
