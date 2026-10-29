package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldDropWeaponsComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19"); light.put("saber","1_155"); light.put("cannon","3_072");
        var dark=new HashMap<String,String>(); dark.put("combo","15_237"); dark.put("droid","14_073"); dark.put("rifle","12_186");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("droid")); return scn;
    }
    private void respond(VirtualTableScenario scn,String text) {
        var combo=scn.GetDSCard("combo");
        for(int i=0;i<24;i++) {
            if(scn.DSGetDecision()!=null && scn.DSGetADParam("cardId")!=null && scn.DSCardPlayAvailable(combo,text)) { scn.DSPlayCard(combo,text); return; }
            if(scn.LSGetDecision()!=null && scn.LSDecisionAvailable("Optional")) scn.LSPass();
            else if(scn.DSGetDecision()!=null && scn.DSDecisionAvailable("Optional")) scn.DSPass();
            else fail("Expected combo response: "+scn.GetCurrentDecision());
        }
        fail("Combo response was never offered");
    }
    @Test public void DropYourWeaponsAndHaltPrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(CardSubtype.USED,card.getCardSubtype()); assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(0,card.getCredits()); scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.EPISODE_I,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
    }
    @Test public void DropYourWeaponsAndHaltResetsLandspeedOnlyUntilEndOfTurn() {
        var scn=scenario(); var luke=scn.GetLSCard("luke"); var combo=scn.GetDSCard("combo");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),luke); scn.SkipToDSTurn(Phase.DEPLOY); assertEquals(1,scn.GetLandspeed(luke));
        scn.DSPlayCard(combo,"Reset"); scn.DSChooseCard(luke); scn.PassAllResponses();
        assertEquals(0,scn.GetLandspeed(luke)); assertTrue(scn.GetDSUsedPile().contains(combo));
        scn.SkipToLSTurn(Phase.ACTIVATE); assertEquals(1,scn.GetLandspeed(luke));
    }
    @Test public void DropYourWeaponsAndHaltReturnsNewCharacterWhenOpponentCannotPay() {
        var scn=scenario(); var luke=scn.GetLSCard("luke"); var combo=scn.GetDSCard("combo"); var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLSHand(luke); scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(3);
        scn.LSPlayCard(luke); scn.LSChooseCard(site); respond(scn,"Make opponent use Force"); scn.PassAllResponses();
        assertTrue(scn.GetLSHand().contains(luke)); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void DropYourWeaponsAndHaltLosesWeaponsAfterBattleDroidBlasterHits() {
        var scn=scenario(); var luke=scn.GetLSCard("luke"); var combo=scn.GetDSCard("combo"); var rifle=scn.GetDSCard("rifle"); var saber=scn.GetLSCard("saber");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),luke); scn.AttachCardsTo(scn.GetDSStartingLocation(),scn.GetLSCard("cannon")); scn.AttachCardsTo(luke,saber); scn.AttachCardsTo(scn.GetDSCard("droid"),rifle);
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3); scn.PrepareDSDestiny(5); scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.DSUseCardAction(rifle,"Fire"); scn.DSChooseCard(luke);
        respond(scn,"weapons lost"); scn.PassAllResponses();
        scn.DSChooseCard(saber); scn.PassAllResponses();
        if(scn.DSHasCardChoiceAvailable(scn.GetLSCard("cannon"))) { scn.DSChooseCard(scn.GetLSCard("cannon")); scn.PassAllResponses(); }
        assertTrue(luke.isHit()); assertTrue(scn.GetLSLostPile().contains(saber)); assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("cannon"))); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
}
