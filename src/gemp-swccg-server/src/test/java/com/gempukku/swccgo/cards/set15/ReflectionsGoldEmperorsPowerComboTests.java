package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldEmperorsPowerComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19"); light.put("obiwan","1_21");
        var dark=new HashMap<String,String>(); dark.put("combo","15_178"); dark.put("emperor","9_109");
        dark.put("throne","9_147"); dark.put("farm","1_294");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("throne")); scn.MoveLocationToTable(scn.GetDSCard("farm"));
        scn.MoveCardsToLocation(scn.GetDSCard("throne"),scn.GetDSCard("emperor"));
        scn.MoveCardsToDSHand(scn.GetDSCard("combo")); scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(scn.GetDSCard("combo"),scn.GetDSCard("throne"));
        return scn;
    }
    @Test public void EmperorsPowerAndOverseeingItPersonallyAddsRelatedDrainOnlyWhileEmperorControlsBattleground() {
        var scn=scenario(); var emperor=scn.GetDSCard("emperor"); var farm=scn.GetDSCard("farm"); var market=scn.GetDSStartingLocation();
        var query=scn.game().getModifiersQuerying();
        assertTrue(query.isProhibitedFromForceDrainingAtLocation(scn.gameState(),scn.GetDSCard("throne"),scn.DS));
        scn.MoveCardsToLocation(farm,scn.GetDSFiller(1));
        float before=query.getForceDrainAmount(scn.gameState(),farm,scn.DS);
        scn.MoveCardsToLocation(market,emperor);
        assertEquals(before+1,query.getForceDrainAmount(scn.gameState(),farm,scn.DS),scn.epsilon);
        scn.MoveCardsToLocation(market,scn.GetLSCard("luke"));
        assertEquals(before,query.getForceDrainAmount(scn.gameState(),farm,scn.DS),scn.epsilon);
    }
    private void addDestiny(boolean loseEffect) {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSFiller(1),scn.GetDSFiller(2),scn.GetLSCard("luke"));
        scn.SkipToPhase(Phase.BATTLE); scn.DSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareDSDestiny(3);
        int before=scn.GetDSBattleDestinyCount(); int reserve=scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(combo,loseEffect?"Lose Effect":"Lose 1 Force"); scn.PassAllResponses();
        if(!loseEffect) { assertTrue(scn.AwaitingDSForceLossPayment()); scn.DSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses(); }
        assertEquals(before+1,scn.GetDSBattleDestinyCount());
        if(loseEffect) assertTrue(scn.GetDSLostPile().contains(combo));
        else { assertEquals(reserve-1,scn.GetDSReserveDeckCount()); assertTrue(combo.getZone().isInPlay()); }
    }
    @Test public void EmperorsPowerAndOverseeingItPersonallyMayLoseEffectForBattleDestiny() { addDestiny(true); }
    @Test public void EmperorsPowerAndOverseeingItPersonallyMayLoseReserveForceForBattleDestiny() { addDestiny(false); }
    @Test public void EmperorsPowerAndOverseeingItPersonallyIsLostWhenEmperorIsForfeited() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var emperor=scn.GetDSCard("emperor"); var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,emperor,scn.GetLSCard("luke"),scn.GetLSCard("obiwan"));
        scn.SkipToPhase(Phase.BATTLE); scn.DSInitiateBattle(site); scn.SkipToDamageSegment(false); scn.PassAllResponses();
        scn.DSChooseCard(emperor); scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(emperor)); assertTrue(scn.GetDSLostPile().contains(combo));
    }
}
