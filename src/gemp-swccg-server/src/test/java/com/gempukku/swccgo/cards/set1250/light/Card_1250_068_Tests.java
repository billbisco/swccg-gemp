package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_068_Tests {
    private static final String STACK = "Stack a Wookiee on Grrrghrrrgh!";
    private static final String DEPLOY = "Deploy a stacked Wookiee to Kashyyyk";
    private static final String DRAW = "Draw cards for the Grrrghrrrgh! stack";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("guide", "1250_68"); put("second", "1250_68"); put("roar", "1116_14");
            put("wookiee", "7_50"); put("chewie", "2_3"); put("other", "1_28"); put("extra", "1_17");
            put("forest", "1116_16"); put("haven", "1116_17");
        }}, new HashMap<>() {{ put("snoova", "10_48"); }}, 35, 35,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario setup(boolean host, boolean locations, boolean darkTurn) {
        var s = scenario(); s.StartGame();
        if (host) s.MoveCardsToLSSideOfTable(s.GetLSCard("roar"));
        if (locations) {
            s.MoveLocationToTable(s.GetLSCard("forest"));
            s.MoveLocationToTable(s.GetLSCard("haven"));
        }
        s.MoveCardsToHand(s.GetLSCard("guide"), s.GetLSCard("second"));
        s.LSActivateForceCheat(5);
        if (darkTurn) {
            s.SkipToDSTurn(Phase.CONTROL);
            if (s.DSAnyDecisionsAvailable()) s.DSPass();
        } else {
            s.SkipToLSTurn(Phase.CONTROL);
        }
        return s;
    }

    private boolean offered(VirtualTableScenario s, PhysicalCardImpl guide, String label) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardPlayAvailable(guide, label);
    }

    private void advanceToFreshMoveDecision(VirtualTableScenario s) {
        String owner = s.GetCurrentPlayer();
        s.SkipToPhase(Phase.MOVE);
        assertEquals(owner, s.GetCurrentPlayer());
        if (owner.equals(s.DS) && s.DSAnyDecisionsAvailable()) s.DSPass();
    }

    @Test
    public void WookieeGuideMatchesFinalUsedPrinting() {
        var s = scenario(); var bp = s.GetLSCard("guide").getBlueprint();
        assertEquals("Wookiee Guide", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertEquals(4, bp.getDestiny(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.ENDOR,
                Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void StackUsesAnActualFriendlyLostWookieeAndLeavesOtherCardsAlone() {
        var s = setup(true, false, false);
        var roar = s.GetLSCard("roar"); var guide = s.GetLSCard("guide");
        s.MoveCardsToTopOfLSLostPile(s.GetLSCard("wookiee"), s.GetLSCard("chewie"), s.GetLSCard("other"));
        s.MoveCardsToTopOfDSLostPile(s.GetDSCard("snoova"));
        advanceToFreshMoveDecision(s);
        assertTrue(offered(s, guide, STACK));
        s.LSPlayCard(guide, STACK); s.PassAllResponses();
        s.LSChooseCard(s.GetLSCard("chewie")); s.PassAllResponses();
        assertEquals(List.of(s.GetLSCard("chewie")), s.GetStackedCards(roar));
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("wookiee")));
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("other")));
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("snoova")));
        assertTrue(s.GetLSUsedPile().contains(guide));
    }

    @Test
    public void NonWookieeInLostPileCannotProvideStackMode() {
        var s = setup(true, false, false);
        s.MoveCardsToTopOfLSLostPile(s.GetLSCard("other"));
        advanceToFreshMoveDecision(s);
        assertFalse(offered(s, s.GetLSCard("guide"), STACK));
    }

    @Test
    public void NoHostDisablesAllThreeModes() {
        var s = setup(false, true, false); var guide = s.GetLSCard("guide");
        s.MoveCardsToTopOfLSLostPile(s.GetLSCard("wookiee"));
        advanceToFreshMoveDecision(s);
        assertFalse(offered(s, guide, STACK)); assertFalse(offered(s, guide, DEPLOY)); assertFalse(offered(s, guide, DRAW));
    }

    private void checkDeployment(boolean darkTurn) {
        var s = setup(true, true, darkTurn);
        var roar = s.GetLSCard("roar"); var guide = s.GetLSCard("guide");
        var chosen = s.GetLSCard("wookiee"); var other = s.GetLSCard("chewie");
        s.StackCardsOn(roar, chosen, other, s.GetLSCard("other"));
        advanceToFreshMoveDecision(s);
        var query = s.game().getModifiersQuerying();
        assertFalse(query.mayDeployAsIfFromHand(s.gameState(), chosen));
        assertTrue(offered(s, guide, DEPLOY));
        int force = s.GetLSForcePileCount();
        s.LSPlayCard(guide, DEPLOY); s.PassAllResponses();
        s.LSChooseCard(chosen); s.PassAllResponses();
        assertTrue(query.mayDeployAsIfFromHand(s.gameState(), chosen));
        assertFalse(query.mayDeployAsIfFromHand(s.gameState(), other));
        assertTrue(s.LSHasCardChoicesAvailable(s.GetLSCard("forest")));
        assertTrue(s.LSHasCardChoicesAvailable(s.GetLSCard("haven")));
        assertFalse(s.LSHasCardChoicesAvailable(s.GetLSStartingLocation()));
        s.LSChooseCard(s.GetLSCard("forest")); s.PassAllResponses();
        assertEquals(s.GetLSCard("forest"), chosen.getAtLocation());
        assertEquals("Printed deploy four, reduced by the actual host's global one", force - 3, s.GetLSForcePileCount());
        assertTrue(s.GetStackedCards(roar).contains(other)); assertTrue(s.GetLSUsedPile().contains(guide));
        s.StackCardsOn(roar, chosen);
        assertFalse(query.mayDeployAsIfFromHand(s.gameState(), chosen));
    }

    @Test
    public void DeployChoosesOnlyTheWookieeAtKashyyykAndPaysNormalCost() { checkDeployment(false); }

    @Test
    public void UsedInterruptMayDirectTheSameLegalDeploymentOnDarkTurn() { checkDeployment(true); }

    @Test
    public void NoKashyyykDestinationDisablesDeploymentMode() {
        var s = setup(true, false, false);
        s.StackCardsOn(s.GetLSCard("roar"), s.GetLSCard("wookiee"));
        advanceToFreshMoveDecision(s);
        assertFalse(offered(s, s.GetLSCard("guide"), DEPLOY));
    }

    @Test
    public void DrawUsesTopXCountingAllStackedCardsAndCannotBeRepeatedWithAnotherCopy() {
        var s = setup(true, false, false);
        var roar = s.GetLSCard("roar"); var guide = s.GetLSCard("guide");
        s.StackCardsOn(roar, s.GetLSCard("wookiee"), s.GetLSCard("other"));
        advanceToFreshMoveDecision(s);
        var expected = s.GetLSReserveDeck().stream().limit(2).toList();
        int hand = s.GetLSHandCount(); int reserve = s.GetLSReserveDeckCount();
        assertTrue(offered(s, guide, DRAW));
        s.LSPlayCard(guide, DRAW); s.PassAllResponses();
        assertTrue(s.GetLSUsedPile().contains(guide)); assertTrue(s.GetLSHand().containsAll(expected));
        assertEquals(hand + 1, s.GetLSHandCount()); assertEquals(reserve - 2, s.GetLSReserveDeckCount());
        assertEquals(2, s.GetStackedCards(roar).size());
        assertFalse(offered(s, s.GetLSCard("second"), DRAW));
    }

    @Test
    public void EmptyStackCannotProvideDrawMode() {
        var s = setup(true, false, false);
        assertFalse(offered(s, s.GetLSCard("guide"), DRAW));
    }
}
