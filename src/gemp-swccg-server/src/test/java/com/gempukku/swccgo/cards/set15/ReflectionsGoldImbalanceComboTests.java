package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldImbalanceComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("scan","3_048"); light.put("grimtaash","1_104"); light.put("retrieve","11_032");
        var dark=new HashMap<String,String>(); dark.put("combo","15_248"); dark.put("site","1_283");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo")); return scn;
    }
    @Test public void ImbalanceAndShockingRevelationPrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(CardSubtype.USED,card.getCardSubtype()); assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(0,card.getCredits()); scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
    }
    @Test public void ImbalanceAndShockingRevelationDisablesScompLinkOnlyForRemainderOfTurn() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var site=scn.GetDSCard("site"); scn.MoveLocationToTable(site); scn.SkipToDSTurn(Phase.DEPLOY);
        var query=scn.game().getModifiersQuerying(); assertEquals(1,query.getIconCount(scn.gameState(),site,Icon.SCOMP_LINK));
        scn.DSPlayCard(combo,"Disable"); scn.DSChooseCard(site); scn.PassAllResponses();
        assertEquals(0,query.getIconCount(scn.gameState(),site,Icon.SCOMP_LINK)); assertTrue(scn.GetDSUsedPile().contains(combo));
        scn.SkipToLSTurn(Phase.ACTIVATE); assertEquals(1,query.getIconCount(scn.gameState(),site,Icon.SCOMP_LINK));
    }
    private void respond(VirtualTableScenario scn,String text) {
        var combo=scn.GetDSCard("combo");
        for(int i=0;i<24;i++) {
            if(scn.DSGetDecision()!=null && scn.DSGetADParam("cardId")!=null && scn.DSCardPlayAvailable(combo,text)) { scn.DSPlayCard(combo,text); return; }
            if(scn.LSGetDecision()!=null && scn.LSDecisionAvailable("Optional")) scn.LSPass();
            else if(scn.DSGetDecision()!=null && scn.DSDecisionAvailable("Optional")) scn.DSPass();
            else fail("Expected response: "+scn.GetCurrentDecision());
        }
        fail("Response not offered");
    }
    @Test public void ImbalanceAndShockingRevelationPunishesHandScanButScanStillCompletes() {
        var scn=scenario(); var scan=scn.GetLSCard("scan"); var combo=scn.GetDSCard("combo");
        scn.MoveCardsToLSHand(scan); scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(1);
        for(int i=1;i<=4;i++) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFiller(i));
        int lost=scn.GetLSLostPileCount(); scn.LSPlayCard(scan,"hand"); respond(scn,"lose Force and card"); scn.PassAllResponses();
        scn.LSChooseCards(); scn.PassAllResponses();
        scn.LSChooseCards(); scn.PassAllResponses();
        assertTrue("Expected loss payment: "+scn.GetCurrentDecision(),scn.AwaitingLSForceLossPayment()); scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scan)); assertEquals(lost+5,scn.GetLSLostPileCount()); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void ImbalanceAndShockingRevelationMakesOpponentLoseTwoAfterRetrievingThree() {
        var scn=scenario(); var retrieve=scn.GetLSCard("retrieve"); var combo=scn.GetDSCard("combo");
        scn.MoveCardsToLSHand(retrieve); scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1),scn.GetLSFiller(2),scn.GetLSFiller(3));
        scn.SkipToLSTurn(Phase.DEPLOY); for(int i=4;i<=7;i++) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFiller(i));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(1));
        scn.LSPlayCard(retrieve); scn.PassAllResponses(); scn.DSChooseOption("Retrieve 1 Force");
        respond(scn,"lose 2"); scn.PassAllResponses(); int before=scn.GetLSLostPileCount();
        assertTrue("Expected loss payment: "+scn.GetCurrentDecision(),scn.AwaitingLSForceLossPayment()); scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses();
        assertEquals(before+3,scn.GetLSLostPileCount()); assertTrue(scn.GetLSLostPile().contains(retrieve)); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void ComponentRestrictionSymbolsMatchPrintedTitles() {
        var card=new com.gempukku.swccgo.cards.set15.dark.Card15_248();
        assertEquals(java.util.List.of(new TitleComponent("Imbalance",Uniqueness.UNRESTRICTED),new TitleComponent("Shocking Revelation",Uniqueness.UNIQUE)),card.getTitleComponents());
    }
}
