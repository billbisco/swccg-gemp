package com.gempukku.swccgo.cards.textstates;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Text states of Dark Rage (13_64o), DFS Squadron Starfighter (14_115o), Watch Your Step (10_26o, 7 side
 * 10_26o_BACK), The Hyperdrive Generator's Gone (12_89o, 7 side 12_89o_BACK) and Flagship (4_122o, 4_122d)
 * (docs/legacy-versions.md, "Text states"). Printed texts are the plain wiki.swccg.com articles, checked against their
 * Decipher archive scans; Flagship's Decipher text is [[Flagship (Errata)]] and Decipher Glossary Version 2.0.
 */
public class FollowUpTextStatesTests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void load() {
        library = new SwccgCardBlueprintLibrary();
    }

    private static VirtualTableScenario scenario(Map<String, String> light, Map<String, String> dark) {
        return new VirtualTableScenario(new HashMap<>(light), new HashMap<>(dark), 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    private static SwccgCardBlueprint assertState(SwccgCardBlueprint state, SwccgCardBlueprint bare, String bareId, String text,
                                                  String label, LocalDate printedOn, LocalDate supersededOn) {
        if (text != null) {
            assertEquals(text, state.getGameText());
            assertNotEquals(bare.getGameText(), state.getGameText());
        } else {
            assertEquals(bare.getGameText(), state.getGameText());
        }
        TextState ts = state.getTextState();
        assertEquals(bareId, ts.currentId());
        assertEquals(label, ts.label());
        assertEquals(printedOn, ts.printedOn());
        assertEquals(supersededOn, ts.supersededOn());
        assertNull(bare.getTextState());
        assertEquals(bare.getTitle(), state.getTitle());
        assertEquals(bare.getUniqueness(), state.getUniqueness());
        assertEquals(bare.getSide(), state.getSide());
        assertEquals(bare.getCardTypes(), state.getCardTypes());
        assertEquals(bare.getLore(), state.getLore());
        assertTrue(bare.getClass().isInstance(state));
        return state;
    }

    private static SwccgCardBlueprint assertState(String id, String text, String label, LocalDate printedOn, LocalDate supersededOn) {
        String bareId = TextState.currentIdOf(id);
        SwccgCardBlueprint state = assertState(library.getSwccgoCardBlueprint(id), library.getSwccgoCardBlueprint(bareId),
                bareId, text, label, printedOn, supersededOn);
        assertEquals(library.getSwccgoCardBlueprint(bareId).getTitle() + " [" + label + "]", GameUtils.getFullName(state));
        return state;
    }

    private static final LocalDate AR_2023 = LocalDate.of(2023, 7, 30);

    // ---------------------------------------------------------------- Dark Rage (13_64o): no Jedi targeted

    @Test
    public void DarkRagePrintedHasItsText() {
        assertState("13_64o", "If opponent just initiated a duel, use 3 Force to cancel it. OR Take Deep Hatred into hand from Reserve Deck; reshuffle. OR If Maul has no combat cards, place an opponent's combat card (random selection) in opponent's Used Pile. (Immune to Sense.)",
                "Printed", LocalDate.of(2001, 10, 31), AR_2023);
    }

    /** Maul (no combat cards) and a Rebel Trooper with a combat card; returns the scenario with Dark Rage in hand. */
    private static VirtualTableScenario darkRage(String id) {
        var scn = scenario(Map.of("rebel", "1_28", "combat", "1_28"), Map.of("rage", id, "maul", "11_54"));
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("maul"), scn.GetLSCard("rebel"));
        scn.StackCardsOn(scn.GetLSCard("rebel"), scn.GetLSCard("combat"));
        scn.GetLSCard("combat").setCombatCard(true);
        scn.MoveCardsToDSHand(scn.GetDSCard("rage"));
        scn.EnsureDSForcePile(3);
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    @Test
    public void DarkRagePrintedPlacesAnyOpponentsCombatCardWithoutTargetingAJedi() {
        var bare = darkRage("13_64");
        assertFalse(bare.DSCardPlayAvailable(bare.GetDSCard("rage"), "combat card"));

        var scn = darkRage("13_64o");
        var rage = scn.GetDSCard("rage");
        assertTrue(scn.DSCardPlayAvailable(rage, "combat card"));
        scn.DSPlayCard(rage, "combat card");
        scn.PassAllResponses();
        assertEquals(Zone.USED_PILE, GameUtils.getZoneFromZoneTop(scn.GetLSCard("combat").getZone()));
    }

    // ---------------------------------------------------------------- DFS Squadron Starfighter (14_115o)

    @Test
    public void DfsSquadronStarfighterPrintedHasArmorAndNoManeuver() {
        var printed = assertState("14_115o", "Deploys -1 to Naboo or same system as your battleship. While at same system as your battleship, may deploy Droid Starfighter Laser Cannons here from Reserve Deck; reshuffle.",
                "Printed", LocalDate.of(2001, 12, 1), AR_2023);
        var bare = library.getSwccgoCardBlueprint("14_115");
        assertEquals(2f, printed.getArmor(), 0.01f);
        assertNull(printed.getManeuver());
        assertNull(bare.getArmor());
        assertEquals(2f, bare.getManeuver(), 0.01f);
        assertEquals(bare.getPower(), printed.getPower(), 0.01f);
        assertEquals(bare.getForfeit(), printed.getForfeit(), 0.01f);
        assertEquals(bare.getDeployCost(), printed.getDeployCost(), 0.01f);
    }

    @Test
    public void DfsSquadronStarfighterPrintedMayDeployMoreThanOneCannonHere() {
        var scn = scenario(Map.of("filler", "1_28"), Map.of("dfs", "14_115o", "battleship", "12_184",
                "naboo", "12_169", "cannon1", "14_127", "cannon2", "14_127"));
        scn.StartGame();
        var naboo = scn.GetDSCard("naboo");
        var dfs = scn.GetDSCard("dfs");
        scn.MoveLocationToTable(naboo);
        scn.MoveCardsToLocation(naboo, dfs, scn.GetDSCard("battleship"));
        scn.EnsureDSForcePile(8);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("cannon1"), scn.GetDSCard("cannon2"));
        assertEquals(2f, scn.game().getModifiersQuerying().getArmor(scn.gameState(), dfs), 0.01f);
        for (int deployed = 1; deployed <= 2; deployed++) {
            assertTrue(scn.DSCardActionAvailable(dfs, "Deploy card from Reserve Deck"));
            scn.DSUseCardAction(dfs, "Deploy card from Reserve Deck");
            assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
            scn.DSChoose(new String[]{scn.DSGetCardChoices().get(scn.DSGetBPChoices().indexOf("14_127"))});
            for (int i = 0; i < 10 && !scn.DSDecisionAvailable("Choose Deploy action"); i++) {
                if (scn.DSAnyDecisionsAvailable())
                    scn.DSPass();
                else
                    scn.LSPass();
            }
            int attached = 0;
            for (String cannon : new String[]{"cannon1", "cannon2"})
                attached += scn.GetDSCard(cannon).getAttachedTo() == dfs ? 1 : 0;
            assertEquals(deployed, attached);
        }
    }

    // ---------------------------------------------------------------- Objectives: printed 7 sides (_BACK)

    @Test
    public void WatchYourStepPrintedFlipsToItsOwnSevenSide() {
        var front = assertState("10_26o", "Deploy Cantina, Docking Bay 94, and Tatooine system. For remainder of game, opponent activates no Force at your Cantina. Your cards with ability are deploy +6 except Luke, smugglers, freighters, and [Independent] starships. Opponent's game text on Kessel system is canceled. While this side up, once during each of your deploy phases, may take Corellia or Kessel into hand from Reserve Deck; reshuffle. Flip this card if you occupy two battlegrounds with smugglers or have completed two Kessel Runs.",
                "Printed", LocalDate.of(2001, 1, 10), AR_2023);
        assertEquals("Card10_026o", front.getClass().getSimpleName());
        var back = library.getSwccgoCardBlueprintBack("10_26o");
        assertEquals("Card10_026o_BACK", back.getClass().getSimpleName());
        assertState(back, library.getSwccgoCardBlueprintBack("10_26"), "10_26_BACK",
                "While this side up, your Force generation is +1 at each system you control with a smuggler. Opponent's Force generation at non-battlegrounds is limited to 1. When you have two or more smugglers in a battle, add one battle destiny. Each of your smugglers is forfeit +2. Once during each turn, may play one interrupt from Lost Pile (then place that card out of play). Sense, Alter, and Keep Your Eyes Open may not be played. Flip this card if you do not occupy two battlegrounds (unless you have completed two Kessel Runs).",
                "Printed", LocalDate.of(2001, 1, 10), AR_2023);
        assertNull(library.getSwccgoCardBlueprintBack("10_26").getTextState());
    }

    @Test
    public void HyperdriveGeneratorsGonePrintedFlipsToItsOwnSevenSide() {
        var front = assertState("12_89o", null, "Printed", LocalDate.of(2001, 8, 1), AR_2023);
        assertEquals("Card12_089o", front.getClass().getSimpleName());
        var back = library.getSwccgoCardBlueprintBack("12_89o");
        assertEquals("Card12_089o_BACK", back.getClass().getSimpleName());
        assertState(back, library.getSwccgoCardBlueprintBack("12_89"), "12_89_BACK",
                "While this side up, your unique (•) Republic characters are power +1 and forfeit +2. Aliens may not have their deploy cost modified and Imperials deploy +1 to Tatooine sites. Whenever you draw battle destiny, may retrieve 1 Force (Force retrieved in this way may be taken into hand.) While Queen's Royal Starship at a planet system, once during each of opponent's control phases may activate up to 2 Force. Once during each of your control phases, opponent loses 1 Force for each battleground site you occupy with a senator.",
                "Printed", LocalDate.of(2001, 8, 1), AR_2023);
        assertNull(library.getSwccgoCardBlueprintBack("12_89").getTextState());
    }

    // ---------------------------------------------------------------- Flagship (4_122o, 4_122d)

    @Test
    public void FlagshipStatesHaveTheirTexts() {
        assertState("4_122o", "Use 2 Force to deploy on one of your Star Destroyers. Your other starships may move as a 'react' to same system (for free). If starship lost, you lose X Force, where X = starship's armor. (Immune to your Alter.)",
                "Printed", LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1));
        assertState("4_122d", "Use 2 Force to deploy on your Star Destroyer. Your other starships may move as a 'react' to same system or sector (for free). If starship lost, you lose X Force, where X = starship's armor. (Immune to your Alter.)",
                "D-E1", LocalDate.of(1998, 11, 1), null);
    }
}
