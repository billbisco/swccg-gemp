package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldWellGuardedComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("objective","7_136"); light.put("indicator","1_122"); light.put("xwing","1_146");
        var dark=new HashMap<String,String>(); dark.put("combo","15_215"); dark.put("system","1_282");
        dark.put("ship","4_167"); dark.put("effect","1_210"); dark.put("interrupt","1_269");
        var scn = new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        scn.MoveOutOfPlay(scn.GetLSCard("objective"));
        return scn;
    }
    @Test public void WellGuardedAndSecurityPrecautionsProtectsEligibleHandCardsOnlyBelowThirteen() {
        var scn=scenario(); scn.StartGame(); var combo=scn.GetDSCard("combo");
        scn.MoveCardsToDSHand(combo); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(combo);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSHand().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        var trooper=scn.GetDSFiller(1); var effect=scn.GetDSCard("effect"); var interrupt=scn.GetDSCard("interrupt");
        scn.MoveCardsToDSHand(trooper,effect,interrupt);
        var query=scn.game().getModifiersQuerying();
        assertTrue(query.isImmuneToCardTitle(scn.gameState(),trooper,Title.Grimtaash));
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),effect,Title.Grimtaash));
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),interrupt,Title.Grimtaash));
        for(int i=2;i<=11;i++) scn.MoveCardsToDSHand(scn.GetDSFiller(i));
        assertEquals(13,scn.GetDSHand().size());
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),trooper,Title.Grimtaash));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(11));
        assertTrue(query.isImmuneToCardTitle(scn.gameState(),trooper,Title.Grimtaash));
    }
    @Test public void WellGuardedAndSecurityPrecautionsProbingHiddenBaseCausesFifteenMinusSystemsForceLoss() {
        var scn=scenario(); scn.StartGame(); var combo=scn.GetDSCard("combo"); var objective=scn.GetLSCard("objective");
        var system=scn.GetDSCard("system"); var probe=scn.GetDSFiller(1);
        scn.MoveCardsToLSSideOfTable(objective); scn.gameState().flipCard(scn.game(),objective,true); objective.setObjectiveDeploymentComplete(true);
        assertEquals("Systems Will Slip Through Your Fingers",objective.getBlueprint().getTitle());
        scn.StackCardsOn(objective,true,scn.GetLSCard("indicator"));
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system,scn.GetDSCard("ship"));
        scn.MoveCardsToDSHand(combo,probe,scn.GetDSFiller(2)); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPlayCardAndPassResponses(combo);
        if(scn.LSGetDecision()!=null) scn.LSPass();
        int before=scn.GetLSLostPileCount();
        scn.DSUseCardAction(objective,"'Probe' a system"); scn.DSChooseCard(system); scn.PassAllResponses();
        scn.DSChooseCard(probe); scn.PassAllResponses();
        assertTrue(scn.AwaitingLSForceLossPayment()); scn.LSPayRemainingForceLossFromReserveDeck();
        assertEquals(before+14,scn.GetLSLostPileCount());
        assertEquals(Zone.OUT_OF_PLAY,objective.getZone());
    }
    @Test public void WellGuardedAndSecurityPrecautionsPreventsBattleDamageAtProbedSystem() {
        var scn=scenario(); scn.StartGame(); var combo=scn.GetDSCard("combo"); var system=scn.GetDSCard("system");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system,scn.GetDSCard("ship"),scn.GetLSCard("xwing"));
        scn.MoveCardsToDSHand(combo); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(combo);
        var probe=scn.GetDSFiller(1); scn.StackCardsOn(system,true,probe); probe.setProbeCard(true);
        scn.EnsureDSForcePile(2); scn.SkipToPhase(Phase.BATTLE); scn.DSInitiateBattle(system); scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isTakesNoBattleDamage(scn.gameState(),scn.DS));
        assertFalse(scn.game().getModifiersQuerying().isTakesNoBattleDamage(scn.gameState(),scn.LS));
        scn.MoveCardsToDSHand(probe);
        assertFalse(scn.game().getModifiersQuerying().isTakesNoBattleDamage(scn.gameState(),scn.DS));
    }
}
