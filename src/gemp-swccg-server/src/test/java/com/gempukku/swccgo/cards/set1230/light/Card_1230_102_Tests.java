package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_102_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tactics", "1230_102");
                    put("xwing", "1_146");
                    put("luke", "1_19");
                    put("tatooine", "1_127");
                    put("red5", "2_71"); put("red2", "2_70");
                    put("han", "1_11"); put("bravo", "14_53");
                    put("forest", "8_72");
                }},
                new HashMap<>() {{
                    put("tie", "1_301"); put("surprise", "5_156");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void DiversionaryTacticsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tactics").getBlueprint();

        assertEquals(Title.Diversionary_Tactics, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DiversionaryTacticsSubtractsOneHyperspeedFromAPilotedStarshipAtASystem() {
        var scn = GetScenario();
        var tactics = scn.GetLSCard("tactics");
        var xwing = scn.GetLSCard("red2");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, luke);
        scn.MoveCardsToLSHand(tactics);
        assertEquals(5, scn.GetHyperspeed(xwing));
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardPlayAvailable(tactics, "Subtract 1 from hyperspeed"));
        scn.LSPlayCard(tactics, "Subtract 1 from hyperspeed");
        scn.LSChooseCard(xwing);
        scn.PassAllResponses();
        assertEquals(4, scn.GetHyperspeed(xwing));
        assertEquals(Zone.TOP_OF_USED_PILE, tactics.getZone());
    }

    @Test
    public void DiversionaryTacticsLostIsNotOfferedWithoutTwoUniqueRebelStarfightersInBattle() {
        var scn = GetScenario();
        var tactics = scn.GetLSCard("tactics");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.MoveCardsToLSHand(tactics);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        assertFalse(scn.LSCardPlayAvailable(tactics, "Draw destiny to exclude a starship"));
    }

    @Test public void DiversionaryTacticsUsedExpiresAtEndOfTurn() {
        var scn = GetScenario(); scn.StartGame(); var ship = scn.GetLSCard("red2"); var system = scn.GetLSCard("tatooine");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship); scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("tactics")); scn.SkipToLSTurn(Phase.MOVE);
        scn.LSPlayCard(scn.GetLSCard("tactics"), "Subtract 1 from hyperspeed"); scn.LSChooseCard(ship); scn.PassAllResponses();
        assertEquals(4, scn.GetHyperspeed(ship)); scn.SkipToDSTurn(Phase.MOVE); assertEquals(5, scn.GetHyperspeed(ship));
    }
    @Test public void DiversionaryTacticsUsedCannotTargetUnpilotedShip() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("tatooine"));
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("red2")); scn.MoveCardsToLSHand(scn.GetLSCard("tactics"));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.LSPlayUsedInterruptAvailable(scn.GetLSCard("tactics")));
    }
    @Test public void DiversionaryTacticsUsedCannotTargetShipLandedAtSite() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("forest"));
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetLSCard("red2")); scn.BoardAsPilot(scn.GetLSCard("red2"), scn.GetLSCard("han"));
        scn.MoveCardsToLSHand(scn.GetLSCard("tactics")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSPlayUsedInterruptAvailable(scn.GetLSCard("tactics")));
    }
    @Test public void DiversionaryTacticsUsedCanReduceOpponentsPilotedShip() {
        var scn = GetScenario(); scn.StartGame(); var system = scn.GetLSCard("tatooine"); var tie = scn.GetDSCard("tie");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, tie); scn.MoveCardsToLSHand(scn.GetLSCard("tactics"));
        scn.SkipToLSTurn(Phase.CONTROL); int before = scn.GetHyperspeed(tie);
        scn.LSPlayCard(scn.GetLSCard("tactics"), "Subtract 1 from hyperspeed"); scn.LSChooseCard(tie); scn.PassAllResponses();
        assertEquals(before - 1, scn.GetHyperspeed(tie));
    }
    @Test public void DiversionaryTacticsLostLowDestinyMayExcludeOpponentsShip() {
        var scn = SpaceBattle("red2", true); scn.PrepareLSDestiny(3);
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("tactics"), "Draw destiny to exclude");
        scn.LSChooseCard(scn.GetDSCard("tie")); scn.PassAllResponses();
        assertFalse(scn.IsParticipatingInBattle(scn.GetDSCard("tie")));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("tactics"));
    }
    @Test public void DiversionaryTacticsLostLowDestinyMayExcludeYourShip() {
        var scn = SpaceBattle("red2", true); scn.PrepareLSDestiny(3);
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("tactics"), "Draw destiny to exclude");
        scn.LSChooseCard(scn.GetLSCard("red2")); scn.PassAllResponses();
        assertFalse(scn.IsParticipatingInBattle(scn.GetLSCard("red2")));
        assertTrue(scn.IsParticipatingInBattle(scn.GetLSCard("red5")));
    }
    @Test public void DiversionaryTacticsLostDestinyFourDoesNotExclude() {
        var scn = SpaceBattle("red2", true); scn.PrepareLSDestiny(4);
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("tactics"), "Draw destiny to exclude");
        assertTrue(scn.IsParticipatingInBattle(scn.GetDSCard("tie"), scn.GetLSCard("red2"), scn.GetLSCard("red5")));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("tactics"));
    }
    @Test public void DiversionaryTacticsLostAcceptsMixedRepublicAndRebelStarfighters() {
        var scn = SpaceBattle("bravo", true); scn.PrepareLSDestiny(3);
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("tactics"), "Draw destiny to exclude");
        scn.LSChooseCard(scn.GetLSCard("bravo")); scn.PassAllResponses();
        assertFalse(scn.IsParticipatingInBattle(scn.GetLSCard("bravo")));
    }
    @Test public void DiversionaryTacticsLostRequiresBothStarfightersToBeUnique() {
        var scn = SpaceBattle("xwing", true); assertFalse(scn.LSPlayLostInterruptAvailable(scn.GetLSCard("tactics")));
    }
    @Test public void DiversionaryTacticsLostRequiresBothStarfightersToBePiloted() {
        var scn = SpaceBattle("red2", false); assertFalse(scn.LSPlayLostInterruptAvailable(scn.GetLSCard("tactics")));
    }
    private VirtualTableScenario SpaceBattle(String other, boolean pilot) {
        var scn = GetScenario(); scn.StartGame(); var system = scn.GetLSCard("tatooine");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, scn.GetLSCard("red5"), scn.GetLSCard(other), scn.GetDSCard("tie"));
        scn.BoardAsPilot(scn.GetLSCard("red5"), scn.GetLSCard("luke"));
        if (pilot && !other.equals("xwing")) scn.BoardAsPilot(scn.GetLSCard(other), scn.GetLSCard("han"));
        scn.MoveCardsToLSHand(scn.GetLSCard("tactics")); scn.EnsureLSForcePile(5); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system); return scn;
    }

    @Test public void DiversionaryTacticsUsedFollowsRetargetedStarship() {
        var scn = GetScenario(); scn.StartGame(); var system = scn.GetLSCard("tatooine"); var red2 = scn.GetLSCard("red2"); var red5 = scn.GetLSCard("red5");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, red2, red5); scn.BoardAsPilot(red2, scn.GetLSCard("luke")); scn.BoardAsPilot(red5, scn.GetLSCard("han"));
        scn.MoveCardsToLSHand(scn.GetLSCard("tactics")); scn.MoveCardsToDSHand(scn.GetDSCard("surprise")); scn.EnsureDSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL); int first = scn.GetHyperspeed(red2); int second = scn.GetHyperspeed(red5);
        scn.LSPlayCard(scn.GetLSCard("tactics"), "Subtract 1 from hyperspeed"); scn.LSChooseCard(red2); scn.LSPass();
        scn.DSPlayCard(scn.GetDSCard("surprise"), "Retarget");
        scn.DSChooseCard(red2);
        scn.DSChooseCard(red5); scn.PassAllResponses();
        assertEquals(first, scn.GetHyperspeed(red2)); assertEquals(second - 1, scn.GetHyperspeed(red5));
    }
}
