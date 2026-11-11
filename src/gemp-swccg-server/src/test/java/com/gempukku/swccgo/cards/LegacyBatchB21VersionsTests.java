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
 * Legacy batch B21 (Virtual Block 8, 11 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text (the 2012 PC card list; Infiltration / Unlikely Allies and Kyle
 * Katarn's Blaster Rifle: the Virtual Cards Master List of 27 April 2013) and every older version is its own 22xx card
 * with the release-sheet slip's text ([Virtual Block 4] / [Virtual Block 8] / ▲ as printed). Rebel Agent's Blaster
 * Rifle keeps its card-list text as PC-E1 (2281_1047, restored: it had been retired and aliased to 1281_47).
 * Images (each version's own text) are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB21VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1281_26", "Adds 2 to the power of anything he pilots. Adds 2 to maneuver of any starfighter he pilots. Your total ability here may not be reduced. Once per game, may lose 1 force to deploy a droid from Lost Pile. During battle, target a just hit character. Target's forfeit is restored to normal."),
            Map.entry("1281_27", "Adds 3 to the power of anything he pilots. Draws one battle destiny if unable to otherwise. During battle, Han may fire a [Virtual Block 8] blaster twice. Once per turn, if opponent is about to draw a weapon destiny targeting Han, they draw two and you choose one. Immune to attrition < 3 (<4 if armed)."),
            Map.entry("1281_30", "Agendas: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 and gain [Republic] here. During your turn, may use 1 Force to take into hand a card with 'Valorum', 'governor' or 'political' in lore from Reserve Deck; reshuffle."),
            Map.entry("1281_32", "Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, 'steal' this Effect (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter.)"),
            Map.entry("1281_33", "If you just forfeited a character (or opponent just drew destiny for Elis Helrot) deploy on table. Whenever you must lose Force from a [Virtual Block 4] objective targeting your non-jedi character, reduce that loss by 1. Elis Herlot and Nabrun Leids are canceled. (Immune to Control)."),
            Map.entry("1281_36", "Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel is power +2 and adds 1 to his total weapon destiny. During your control phase, may draw destiny. If destiny > 3, your Force drains at same and related locations are +1 for remainder of turn."),
            Map.entry("1281_44", "Infiltration: Deploy Nar Shaddaa, Undercity and Scoundrel's Luck. For reminder of game, your gamblers, information brokers, and spies are scoundrels. You may not deploy Nightfall or cards with ability except scoundrels and [Independent] starships. 'Luke' on opponent's [Reflections II] objective is treated as 'Kyle Katarn.' Once per turn, may deploy a battleground site to Nar Shaddaa from Reserve Deck; reshuffle. While this side up, your Force drains at systems are -1. May use 1 Force to take Imperial Navigation Charts into hand from Reserve Deck; reshuffle. Flip this card if Imperial Navigation Charts is 'stolen.' Unlikely Allies: While this side up, your scoundrels are forfeit +1. Your Force generation is +1 for each battleground you occupy with a scoundrel (to a maximum of +2). 'Once' on Scoundrel's Luck is treated as 'twice.' During your control phase, may retrieve 1 Force. Once during your turn, may place a card from hand in your Used Pile or Lost Pile, shuffle that pile and take the top card into hand. Flip this card if Imperial Navigation Charts is not on table (unless you have completed a [Virtual Block 8] Kessel Run)."),
            Map.entry("1281_46", "Deploy on Han. I've Got a Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value."),
            Map.entry("1281_47", "Deploy on Kyle Katarn. May target a character, creature or vehicle for free. Draw destiny (two if Scoundrel's Luck on table). Character or creature hit, and its forfeit = 0, if total destiny > defense value. Vehicle hit if total destiny -2 > defense value."),
            Map.entry("1282_11", "Deploy on an assassin; assassin is power +1. May target a character at same or adjacent site for free. Draw destiny. If destiny +1 > defense value: target hit, may not be captured, and you may place a bounty on target in Used Pile to retrieve 2 Force."),
            Map.entry("1282_12", "Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once during battle, where present, may steal an opponent's just-lost lightsaber. Immune to attrition < 3 (< 5 while armed with a 'stolen' lightsaber)."));

    /** version id -> {current printing id, label, game text, printed on, superseded on} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2281_26", new String[]{"1281_26", "", "[Pilot] 2. Adds 2 to maneuver of any starfighter he pilots. Your total ability here may not be reduced. Once per game, may lose 1 Force to deploy a droid from Lost Pile. During battle, may target a just-hit character. Target's forfeit is restored to normal.", "2012, 5, 11", "2012, 7, 21"}),
            Map.entry("2281_27", new String[]{"1281_27", "", "Adds 3 to power of anything he pilots. Draws one battle destiny if unable to otherwise. During battle, may fire a [Virtual Block 8] blaster twice. Once per turn, if opponent is about to draw weapon destiny targeting Han, they draw two and you choose one. Immune to attrition < 3 (< 4 if armed).", "2012, 5, 11", "2012, 12, 12"}),
            Map.entry("2281_30", new String[]{"1281_30", "", "Agenda: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 (and gain [Republic]) here. During your turn, may use 1 Force to ▲ a card with 'governor,' 'political,' or 'Valorum' in lore.", "2012, 5, 11", "2012, 7, 21"}),
            Map.entry("2281_32", new String[]{"1281_32", "PC-E4", "Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, 'steal' this Effect (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter.)", "2013, 4, 27", "2013, 5, 7"}),
            Map.entry("2281_33", new String[]{"1281_33", "", "If you just forfeited a character (or opponent just drew destiny for Elis Helrot) deploy on table. Whenever you must lose Force from a [Virtual Block 4] objective targeting your non-Jedi character, reduce loss by 1. Elis Helrot and Nabrun Leids are canceled. (Immune to Control.)", "2012, 5, 11", "2012, 7, 21"}),
            Map.entry("2281_36", new String[]{"1281_36", "", "Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel is power +2 and his total weapon destiny is +1. During your control phase, may draw destiny. If destiny > 3, your Force drains at same and related locations are +1 for remainder of turn.", "2012, 5, 11", "2012, 7, 21"}),
            Map.entry("2281_44", new String[]{"1281_44", "", "Infiltration: Deploy Nar Shaddaa, Undercity and Scoundrel's Luck. For reminder of game, your gamblers, information brokers, and spies are scoundrels. You may not deploy Nightfall or cards with ability except scoundrels and [Independent] starships. 'Luke' on opponent's [Reflections II] objective is treated as 'Rebel Agent.' Once per turn, may deploy a battleground site to Nar Shaddaa from Reserve Deck; reshuffle. While this side up, your Force drains at systems are -1. May use 1 Force to take Imperial Navigation Charts into hand from Reserve Deck; reshuffle. Flip this card if Imperial Navigation Charts is 'stolen.' Unlikely Allies: While this side up, your scoundrels are forfeit +1. Your Force generation is +1 for each battleground you occupy with a scoundrel (to a maximum of +2). 'Once' on Scoundrel's Luck is treated as 'twice.' During your control phase, may retrieve 1 Force. Once during your turn, may place a card from hand in your Used Pile or Lost Pile, shuffle that pile and take the top card into hand. Flip this card if Imperial Navigation Charts is not on table (unless you have completed a [Virtual Block 8] Kessel Run).", "2012, 5, 11", "2013, 4, 27"}),
            Map.entry("2281_46", new String[]{"1281_46", "", "Deploy on Han. I've Got A Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, its forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.", "2012, 5, 11", "2012, 7, 21"}),
            Map.entry("2281_47", new String[]{"1281_47", "", "Deploy on Rebel Agent. May target a character, creature or vehicle for free. Draw destiny (two if Scoundrel's Luck on table). Character or creature hit, and its forfeit = 0, if total destiny > defense value. Vehicle hit if total destiny -2 > defense value.", "2012, 5, 11", "2012, 7, 21"}),
            Map.entry("2281_1047", new String[]{"1281_47", "PC-E1", "Deploy on Rebel Agent. May target a character, creature or vehicle for free. Draw destiny (two if Scoundrel's Luck on table). Character or Creature hit, and forfeit = 0, if destiny > defense value. Vehicle hit, if destiny -2 > defense value.", "2012, 7, 21", "2013, 4, 27"}),
            Map.entry("2282_11", new String[]{"1282_11", "", "Deploy on your assassin; assassin is power +1. May target a character at same or adjacent site for free. Draw destiny. If destiny +1 > defense value: target hit, may not be captured this turn, and you may place a bounty on target in Used Pile to retrieve 2 Force.", "2012, 9, 14", "2012, 12, 12"}),
            Map.entry("2282_12", new String[]{"1282_12", "", "Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once per turn, where present, may steal an opponent's just-lost lightsaber. Immune to attrition < 3 (< 5 while armed with a 'stolen' lightsaber).", "2012, 9, 14", "2012, 12, 12"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    private static LocalDate date(String ymd) {
        String[] p = ymd.split(", ");
        return LocalDate.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
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
        assertEquals(12, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertEquals(id, id, library.getBaseBlueprintId(id));
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, date(v[3]), version.printedOn());
            assertEquals(id, date(v[4]), version.supersededOn());
        });
    }

    @Test
    public void RebelAgentsBlasterRifleVersionsKeepTheirPrintedTitle() {
        for (String id : new String[]{"2281_47", "2281_1047"}) {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertEquals(id, "Rebel Agent's Blaster Rifle", card.getPrintedTitle());
            assertEquals(id, "Kyle Katarn's Blaster Rifle", card.getTitle());
        }
        assertNull(library.getSwccgoCardBlueprint("1281_47").getPrintedTitle());
    }
}
