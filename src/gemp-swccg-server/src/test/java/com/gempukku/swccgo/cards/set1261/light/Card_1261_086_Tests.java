package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_086_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("reflection", "1261_86"), Map.entry("endorLeia", "8_8"), Map.entry("otherLeia", "1_17"),
                Map.entry("objective", "7_138"), Map.entry("lost", "1_66"), Map.entry("jungle", "4_86"),
                Map.entry("yoda", "4_2"), Map.entry("test", "4_77"))),
                new HashMap<>(), 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.MoveOutOfPlay(scn.GetLSCard("objective"));
        scn.StartGame();
        return scn;
    }

    @Test public void reflectionUsesTheExactFinalLegacyPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("reflection").getBlueprint();
        assertEquals("Reflection", b.getTitle()); assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.DEPLOYS_ON_CHARACTERS)));
    }

    private VirtualTableScenario deploy(boolean endor) {
        var scn = scenario(); var leia = scn.GetLSCard(endor ? "endorLeia" : "otherLeia");
        var reflection = scn.GetLSCard("reflection");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), leia);
        scn.MoveCardsToLSHand(reflection);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(endor, scn.LSCardPlayAvailable(reflection));
        if (endor) {
            scn.LSPlayCard(reflection);
            if (scn.LSHasCardChoiceAvailable(leia)) scn.LSChooseCard(leia);
            scn.PassAllResponses();
            assertEquals(leia, reflection.getAttachedTo());
        }
        return scn;
    }

    @Test public void reflectionActuallyDeploysOnlyOnEndorLeia() {
        deploy(true); deploy(false);
    }

    private void retrieve(boolean saveBack, int cost) {
        var scn = deploy(true); var reflection = scn.GetLSCard("reflection");
        var objective = scn.GetLSCard("objective"); var lost = scn.GetLSCard("lost");
        scn.MoveCardsToLSSideOfTable(objective);
        if (saveBack) scn.gameState().flipCard(scn.game(), objective, true);
        scn.MoveCardsToTopOfLSLostPile(lost);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("to retrieve 1 Force")));
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(6);
        assertEquals(saveBack, scn.LSCardActionAvailable(reflection, "Use 1 Force"));
        assertTrue(scn.LSCardActionAvailable(reflection, "Use 3 Force"));
        int before = scn.GetLSForcePileCount();
        scn.LSUseCardAction(reflection, "Use " + cost + " Force");
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(lost));
        assertEquals(before - cost, scn.GetLSForcePileCount());
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("to retrieve 1 Force")));
    }

    @Test public void reflectionRetrievesForThreeWithTheObjectiveFront() { retrieve(false, 3); }
    @Test public void reflectionMayUseOneWithTheActualObjectiveBack() { retrieve(true, 1); }
    @Test public void reflectionMayStillChooseThreeWithTheBack() { retrieve(true, 3); }

    private boolean completeTest(boolean boost) {
        var scn = scenario(); var site = scn.GetLSCard("jungle"); var leia = scn.GetLSCard("endorLeia");
        var yoda = scn.GetLSCard("yoda"); var test = scn.GetLSCard("test");
        scn.MoveLocationToTable(site); scn.MoveCardsToLocation(site, leia, yoda);
        scn.AttachCardsTo(site, test);
        test.setTargetedCard(TargetId.JEDI_TEST_MENTOR, null, yoda, Filters.sameCardId(yoda));
        test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, leia, Filters.sameCardId(leia));
        if (boost) scn.AttachCardsTo(leia, scn.GetLSCard("reflection"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(7);
        assertTrue(scn.GetLSAvailableActions().toString(), scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Attempt Jedi Test")));
        scn.LSUseCardAction(test, "Attempt Jedi Test");
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        return GameConditions.isJediTestCompleted(scn.game(), test);
    }

    @Test public void trainingDestinySevenBeatsTheActualFirstTestOnlyWithReflection() {
        assertTrue(completeTest(true));
        assertFalse(completeTest(false));
    }
}
