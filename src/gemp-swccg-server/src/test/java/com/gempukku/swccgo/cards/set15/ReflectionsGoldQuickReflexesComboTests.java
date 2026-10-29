package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldQuickReflexesComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("effect","15_200"); dark.put("boba","5_091"); dark.put("hidden","15_244"); dark.put("otherhidden","6_154"); dark.put("blaster","1_317");
        dark.put("prisoner","15_303"); dark.put("oota","2_137"); dark.put("site1","6_163"); dark.put("site2","6_164"); dark.put("site3","6_165");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("effect"));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(scn.GetDSCard("effect")); return scn;
    }
    @Test public void QuickReflexesAndAllWrappedUpPrintedMetadataAndConditionalImmunity() {
        var scn=scenario(); var effect=scn.GetDSCard("effect"); var card=effect.getBlueprint(); var query=scn.game().getModifiersQuerying();
        assertEquals(List.of(new TitleComponent("Quick Reflexes",Uniqueness.UNRESTRICTED),new TitleComponent("All Wrapped Up",Uniqueness.UNIQUE)),card.getTitleComponents());
        assertEquals(2f,card.getDestiny(),scn.epsilon); assertEquals(Uniqueness.UNIQUE,card.getUniqueness()); assertEquals(0,card.getCredits());
        scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
        scn.MoveCardsToDSHand(scn.GetDSCard("prisoner"),scn.GetDSCard("oota"));
        assertTrue(query.isImmuneToCardTitle(scn.gameState(),scn.GetDSCard("prisoner"),Title.Sense));
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),scn.GetDSCard("oota"),Title.Sense));
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),effect,Title.Alter));
        for(int i=1;i<=3;i++) { var site=scn.GetDSCard("site"+i); scn.MoveLocationToTable(site); scn.MoveCardsToLocation(site,scn.GetDSFiller(i)); }
        assertTrue(query.isImmuneToCardTitle(scn.gameState(),effect,Title.Alter));
        scn.MoveCardsToDSHand(scn.GetDSFiller(3)); assertFalse(query.isImmuneToCardTitle(scn.gameState(),effect,Title.Alter));
    }
    @Test public void QuickReflexesAndAllWrappedUpRetrievesHiddenWeaponsComboButNotBlasterForTwoForce() {
        var scn=scenario(); var effect=scn.GetDSCard("effect"); var hidden=scn.GetDSCard("hidden"); var blaster=scn.GetDSCard("blaster");
        scn.MoveCardsToTopOfDSLostPile(hidden,blaster,scn.GetDSCard("otherhidden")); scn.EnsureDSForcePile(2); scn.SkipToPhase(Phase.DRAW);
        int force=scn.GetDSForcePileCount(); scn.DSUseCardAction(effect,"Take Hidden Weapons"); scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(hidden)); assertThrows(RuntimeException.class,()->scn.DSChooseCard(blaster));
        scn.DSChooseCard(hidden); scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(hidden)); assertTrue(scn.GetDSLostPile().contains(blaster)); assertEquals(force-2,scn.GetDSForcePileCount());
    }
    @Test public void QuickReflexesAndAllWrappedUpCapturesOpponentsForfeitedCharacterWithBountyHunter() {
        var scn=scenario(); var effect=scn.GetDSCard("effect"); var boba=scn.GetDSCard("boba"); var luke=scn.GetLSCard("luke"); var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,boba,scn.GetDSFiller(1),scn.GetDSFiller(2),luke); scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.BATTLE); scn.DSInitiateBattle(site); scn.SkipToDamageSegment(false);
        boolean played=false;
        for(int i=0;i<24 && !played;i++) {
            if(scn.DSGetDecision()!=null && scn.DSGetADParam("cardId")!=null && scn.DSCardActionAvailable(effect,"Capture")) { scn.DSUseCardAction(effect,"Capture"); played=true; }
            else if(scn.AwaitingLSBattleDamagePayment()) scn.LSChooseCard(luke);
            else if(scn.LSGetDecision()!=null && scn.LSDecisionAvailable("Optional")) scn.LSPass();
            else if(scn.DSGetDecision()!=null && scn.DSDecisionAvailable("Optional")) scn.DSPass();
            else fail("Expected forfeiture response: "+scn.GetCurrentDecision());
        }
        assertTrue(played);
        for(int i=0;i<16 && !luke.isCaptive();i++) {
            if(scn.DSCaptureDecisionAvailable()) scn.DSChooseSeizeCaptive();
            else if(scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
            else if(scn.DSHasCardChoiceAvailable(boba)) scn.DSChooseCard(boba);
            else if(scn.LSGetDecision()!=null && scn.LSDecisionAvailable("Optional")) scn.LSPass();
            else if(scn.DSGetDecision()!=null && scn.DSDecisionAvailable("Optional")) scn.DSPass();
            else fail("Expected capture response: "+scn.GetCurrentDecision());
        }
        assertTrue(luke.isCaptive()); assertEquals(boba,luke.getAttachedTo()); assertFalse(scn.GetLSLostPile().contains(luke));
    }
    @Test
    public void QuickReflexesAndAllWrappedUpMakesWeHaveAPrisonerActuallyFree() {
        var light = new HashMap<String, String>();
        light.put("luke", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("vader", "1_168");
        dark.put("effect", "15_200");
        dark.put("prisoner", "15_303");
        var scn = new VirtualTableScenario(light, dark);
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var prisoner = scn.GetDSCard("prisoner");
        scn.StartGame();
        scn.MoveCardsToDSHand(scn.GetDSCard("effect"));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(scn.GetDSCard("effect"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, luke);
        scn.MoveCardsToDSHand(prisoner);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(3);
        scn.PrepareDSDestiny(6);
        scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.SkipToDamageSegment(false);

        boolean played = false;
        int forceBefore = -1;
        for (int i = 0; i < 24 && !played; i++) {
            if (scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null
                    && scn.DSCardPlayAvailable(prisoner)) {
                forceBefore = scn.GetDSForcePileCount();
                scn.DSPlayCard(prisoner);
                played = true;
            } else if (scn.LSGetDecision() != null
                    && (scn.AwaitingLSBattleDamagePayment() || scn.AwaitingLSAttritionPayment())
                    && scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
            } else if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("Optional")) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("Optional")) {
                scn.DSPass();
            } else {
                fail("Expected forfeiture or its capture-response window: " + scn.GetCurrentDecision());
            }
        }
        assertTrue("Capture must be played from the real forfeiture response", played);
        for (int i = 0; i < 24 && !luke.isCaptive(); i++) {
            if (scn.DSCaptureDecisionAvailable()) {
                scn.DSChooseSeizeCaptive();
            } else if (scn.DSHasCardChoiceAvailable(vader)) {
                scn.DSChooseCard(vader);
            } else if (scn.DSHasCardChoiceAvailable(luke)) {
                scn.DSChooseCard(luke);
            } else if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("Optional")) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("Optional")) {
                scn.DSPass();
            } else {
                fail("Expected capture choice or response: " + scn.GetCurrentDecision());
            }
        }
        scn.PassAllResponses();
        assertTrue(luke.isCaptive());
        assertEquals(vader, luke.getAttachedTo());
        assertFalse(scn.GetLSLostPile().contains(luke));
        assertEquals(forceBefore, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSLostPile().contains(prisoner));
    }
}
