package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;
public class ReflectionsGoldElisOotaComboTests {
    private void respondWith(VirtualTableScenario s, PhysicalCardImpl card, String action) {
        for(int i=0;i<30&&!(s.DSGetDecision()!=null&&s.DSCardPlayAvailable(card,action));i++) {
            var decision=s.GetCurrentDecision();
            assertNotNull("Expected response window for "+action,decision);
            assertTrue("Unexpected decision: "+decision.getText(),decision.getText().toLowerCase().contains("optional"));
            if(s.DSGetDecision()!=null) s.DSPass(); else s.LSPass();
        }
        assertTrue("Expected printed response "+action,s.DSCardPlayAvailable(card,action));
        s.DSPlayCard(card,action);
    }
    @Test public void ElisHelrotAndOotaGootaSoloCapturesRebelsTransportedByNabrunLeids() {
        var light=new HashMap<String,String>(); light.put("nabrun","1_97"); light.put("han","1_11");
        var dark=new HashMap<String,String>(); dark.put("card","15_238"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var nabrun=s.GetLSCard("nabrun"); var han=s.GetLSCard("han");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(nabrun); s.MoveCardsToLocation(s.GetLSStartingLocation(),han); s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader"));
        s.SkipToLSTurn(Phase.CONTROL); s.EnsureDSForcePile(3); int force=s.GetDSForcePileCount(); s.PrepareLSDestiny(0);
        s.LSPlayCard(nabrun); s.LSChooseCard(s.GetLSStartingLocation()); s.LSChooseCard(s.GetDSStartingLocation()); s.LSChooseCard(han); s.PassAllResponses(); s.LSChooseYes();
        respondWith(s,card,"capture Rebels"); s.PassAllResponses();
        assertTrue(s.DSDecisionAvailable("capturing")); s.DSChooseSeizeCaptive(); s.PassAllResponses();
        assertTrue(han.isCaptive()); assertEquals(s.GetDSCard("vader"),han.getAttachedTo()); assertTrue(s.GetLSLostPile().contains(nabrun)); assertEquals(force-2,s.GetDSForcePileCount()); assertTrue(s.GetDSLostPile().contains(card));
    }

    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("han","1_11"); light.put("run","1_052"); light.put("kessel","1_126"); light.put("hoth","3_055"); light.put("ship","9_077");
        var dark=new HashMap<String,String>(); dark.put("combo","15_238"); dark.put("vader","1_168"); dark.put("site","1_290");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo"));
        scn.MoveLocationToTable(scn.GetDSCard("site")); scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("vader")); return scn;
    }
    @Test public void ElisHelrotAndOotaGootaSoloPrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(CardSubtype.LOST,card.getCardSubtype()); assertEquals(Uniqueness.UNRESTRICTED,card.getUniqueness());
        assertEquals(0,card.getCredits()); scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
        assertEquals(List.of("Elis Helrot",Title.Oota_Goota_Solo),card.getTitles());
    }
    private void transport(boolean pay) {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var vader=scn.GetDSCard("vader"); var from=scn.GetDSStartingLocation(); var to=scn.GetDSCard("site");
        scn.SkipToDSTurn(Phase.CONTROL); scn.EnsureDSForcePile(3); scn.PrepareDSDestiny(2); int before=scn.GetDSForcePileCount();
        scn.DSPlayCard(combo,"'Transport'"); scn.DSChooseCard(from); scn.DSChooseCard(to); scn.DSChooseCard(vader); scn.PassAllResponses();
        if(pay) scn.DSChooseYes(); else scn.DSChooseNo(); scn.PassAllResponses();
        assertEquals(pay?to:from,vader.getAtLocation()); assertEquals(before-(pay?2:0),scn.GetDSForcePileCount()); assertTrue(scn.GetDSLostPile().contains(combo));
    }
    @Test public void ElisHelrotAndOotaGootaSoloTransportsForDrawnForceCostAndStillGoesLost() { transport(true); }
    @Test public void ElisHelrotAndOotaGootaSoloDeclinedTransportLeavesCharactersAndGoesLost() { transport(false); }
    @Test public void ElisHelrotAndOotaGootaSoloStopsPreviouslyDeployedSmugglerForTurn() {
        var scn=scenario(); var han=scn.GetLSCard("han"); var combo=scn.GetDSCard("combo"); scn.MoveCardsToLocation(scn.GetDSCard("site"),han);
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSPlayCard(combo,"Prevent a smuggler"); scn.DSChooseCard(han); scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(),han)); assertTrue(scn.GetDSLostPile().contains(combo));
        scn.SkipToLSTurn(Phase.ACTIVATE); assertFalse(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(),han));
    }
    @Test public void ElisHelrotAndOotaGootaSoloCancelsKesselRunAndGoesLost() {
        var scn=scenario(); var run=scn.GetLSCard("run"); var combo=scn.GetDSCard("combo"); var han=scn.GetLSCard("han");
        scn.MoveLocationToTable(scn.GetLSCard("kessel")); scn.MoveLocationToTable(scn.GetLSCard("hoth"));
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"),scn.GetLSCard("ship")); scn.BoardAsPilot(scn.GetLSCard("ship"),han);
        scn.MoveCardsToLSHand(run); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(run); scn.LSChooseCard(scn.GetLSCard("kessel")); scn.LSChooseCard(han); scn.PassAllResponses();
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSPlayCard(combo,"Cancel"); scn.DSChooseCard(run); scn.PassAllResponses();
        assertTrue("Kessel Run zone="+run.getZone()+" decision="+scn.GetCurrentDecision(),scn.GetLSLostPile().contains(run)); assertTrue("Combo zone="+combo.getZone(),scn.GetDSLostPile().contains(combo));
    }
}
