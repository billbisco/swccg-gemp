package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_019_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("swamp", "4_87"); put("jungle", "4_86");
                    put("trash", "1_125"); put("food", "3_70"); }},
                new HashMap<>() {{ put("dianoga", "1261_19"); put("bantha", "1_307"); put("yaggle", "3_142");
                    put("waters", "2_119"); put("wampa", "3_93"); put("cave", "3_150"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void DianogaPrintedStatsAndUnrestrictedUniquenessMatchPublishedSheet() {
        var scn = scenario(); var b = scn.GetDSCard("dianoga").getBlueprint();
        assertEquals("Dianoga", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertEquals(0, b.getForfeit(), scn.epsilon); assertNull(b.getFerocity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.CREATURE); }});
        scn.BlueprintModelTypeCheck(b, new ArrayList<>() {{ add(ModelType.SWAMP); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.CREATURE); add(Icon.A_NEW_HOPE); add(Icon.LEGACY_BLOCK_6); }});
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("dianoga"));
        assertEquals(4, scn.GetDefense(scn.GetDSCard("dianoga")));
    }

    @Test
    public void DianogaDeploysOnlyToPrintedHabitatsIncludingDarkWaters() {
        var scn = scenario(); scn.StartGame(); var d = scn.GetDSCard("dianoga"); var ordinary = scn.GetDSStartingLocation();
        var swamp = scn.GetLSCard("swamp"); var jungle = scn.GetLSCard("jungle"); var trash = scn.GetLSCard("trash");
        scn.MoveLocationToTable(swamp); scn.MoveLocationToTable(jungle); scn.MoveLocationToTable(trash);
        scn.MoveCardsToDSHand(d); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSDeployCard(d);
        assertTrue(scn.DSHasCardChoiceAvailable(swamp)); assertTrue(scn.DSHasCardChoiceAvailable(jungle));
        assertTrue(scn.DSHasCardChoiceAvailable(trash)); assertFalse(scn.DSHasCardChoiceAvailable(ordinary));
        scn.DSChooseCard(swamp); scn.PassAllResponses(); assertEquals(swamp, d.getAtLocation());
        assertEquals(5, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), d, 3f), scn.epsilon);
    }

    @Test
    public void DianogaCanActuallyDeployToDarkWatersAtOtherwiseInvalidSite() {
        var scn = scenario(); scn.StartGame(); var d = scn.GetDSCard("dianoga"); var site = scn.GetDSStartingLocation();
        scn.AttachCardsTo(site, scn.GetDSCard("waters")); scn.MoveCardsToDSHand(d); scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount(); scn.DSDeployCard(d);
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        scn.PassAllResponses(); assertEquals(site, d.getAtLocation()); assertEquals(force - 4, scn.GetDSForcePileCount());
    }

    private void feed(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl food,
                      com.gempukku.swccgo.game.PhysicalCardImpl creature) {
        var yaggle = scn.GetDSCard("yaggle"); scn.MoveCardsToDSHand(yaggle);
        scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(5);
        scn.DSPlayCard(yaggle); scn.DSChooseCard(food); scn.DSChooseCard(creature);
        scn.PassCardPlayResponses(); scn.PassForceUseResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
    }

    @Test
    public void DianogaEatingOpponentsCardForcesExactlyTwoAdditionalForceLoss() {
        var scn = scenario(); scn.StartGame(); var swamp = scn.GetLSCard("swamp"); scn.MoveLocationToTable(swamp);
        var d = scn.GetDSCard("dianoga"); var food = scn.GetLSCard("food"); scn.MoveCardsToLocation(swamp, d, food);
        int lost = scn.GetLSLostPileCount(); feed(scn, food, d);
        for (int i = 0; i < 2; i++) { assertTrue(scn.AwaitingLSForceLossPayment()); scn.LSPayForceLossFromReserveDeck(); scn.PassAllResponses(); }
        assertFalse(scn.AwaitingLSForceLossPayment()); assertEquals(lost + 3, scn.GetLSLostPileCount());
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, food);
    }

    @Test
    public void DianogaEatingOwnCardDoesNotCauseOpponentsForceLoss() {
        var scn = scenario(); scn.StartGame(); var swamp = scn.GetLSCard("swamp"); scn.MoveLocationToTable(swamp);
        var d = scn.GetDSCard("dianoga"); var food = scn.GetDSCard("bantha"); scn.MoveCardsToLocation(swamp, d, food);
        int lost = scn.GetLSLostPileCount(); feed(scn, food, d);
        assertFalse(scn.AwaitingLSForceLossPayment()); assertEquals(lost, scn.GetLSLostPileCount());
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, food);
    }

    @Test
    public void DianogaDoesNotCauseForceLossWhenAnotherCreatureEatsOpponentsCard() {
        var scn = scenario(); scn.StartGame(); var swamp = scn.GetLSCard("swamp"); var cave = scn.GetDSCard("cave");
        scn.MoveLocationToTable(swamp); scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(swamp, scn.GetDSCard("dianoga")); var food = scn.GetLSCard("food");
        scn.MoveCardsToLocation(cave, food, scn.GetDSCard("wampa")); int lost = scn.GetLSLostPileCount();
        feed(scn, food, scn.GetDSCard("wampa")); assertFalse(scn.AwaitingLSForceLossPayment());
        assertEquals(lost + 1, scn.GetLSLostPileCount());
    }
}
