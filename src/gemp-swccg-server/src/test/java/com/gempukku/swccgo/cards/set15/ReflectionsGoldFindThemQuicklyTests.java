package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldFindThemQuicklyTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("amidala","12_022"); light.put("jedi","11_010");
        var dark=new HashMap<String,String>(); dark.put("effect","15_184"); dark.put("maul","11_054"); dark.put("trace","11_080");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("effect")); return scn;
    }
    @Test public void IWillFindThemQuicklyMasterPrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("effect").getBlueprint();
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(2,card.getCredits()); assertTrue(card.isHorizontal());
        assertEquals(Uniqueness.UNIQUE,card.getUniqueness()); assertEquals(Rarity.UNKNOWN,card.getRarity()); assertEquals("",card.getLore());
        scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.EPISODE_I,Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card,List.of());
    }
    @Test public void IWillFindThemQuicklyMasterUploadsTraceOnlyOncePerGame() {
        var scn=scenario(); var effect=scn.GetDSCard("effect"); var trace=scn.GetDSCard("trace");
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(effect);
        scn.MoveCardsToTopOfDSReserveDeck(trace); if(scn.LSGetDecision()!=null) scn.LSPass();
        scn.DSUseCardAction(effect,"Take card"); scn.PassAllResponses(); scn.DSChooseCard(trace); scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(trace));
        scn.MoveCardsToTopOfDSReserveDeck(trace); scn.SkipToDSTurn(Phase.DEPLOY);
        assertNotNull(scn.DSGetDecision()); assertFalse(scn.DSCardActionAvailable(effect,"Take card"));
    }
    @Test public void IWillFindThemQuicklyMasterCapturesAmidalaOnlyWhenNoJediIsWithMaul() {
        var scn=scenario(); var effect=scn.GetDSCard("effect"); var maul=scn.GetDSCard("maul"); var amidala=scn.GetLSCard("amidala"); var jedi=scn.GetLSCard("jedi");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),maul,amidala,jedi);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(effect); assertFalse(amidala.isCaptive());
        scn.MoveCardsToLSHand(jedi);
        if(scn.LSGetDecision()!=null) scn.LSPass();
        if(scn.DSGetDecision()!=null && !scn.DSCaptureDecisionAvailable()) scn.DSPass();
        for(int i=0;i<16 && !amidala.isCaptive();i++) {
            if(scn.DSCaptureDecisionAvailable()) scn.DSChooseSeizeCaptive();
            else if(scn.DSHasCardChoiceAvailable(maul)) scn.DSChooseCard(maul);
            else if(scn.DSGetDecision()!=null && scn.DSDecisionAvailable("Optional")) scn.DSPass();
            else if(scn.LSGetDecision()!=null && scn.LSDecisionAvailable("Optional")) scn.LSPass();
            else fail("Expected capture response: "+scn.GetCurrentDecision());
        }
        assertTrue(amidala.isCaptive()); assertEquals(maul,amidala.getAttachedTo());
    }
    @Test public void IWillFindThemQuicklyMasterLetsOpponentDeployAmidalaFromReserve() {
        var scn=scenario(); var effect=scn.GetDSCard("effect"); var amidala=scn.GetLSCard("amidala");
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(effect);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(8); scn.MoveCardsToTopOfLSReserveDeck(amidala);
        scn.LSUseCardAction(effect,"Deploy Amidala"); scn.PassAllResponses();
        scn.LSChooseCard(amidala); scn.PassAllResponses(); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertTrue(amidala.getZone().isInPlay()); assertFalse(scn.GetLSReserveDeck().contains(amidala));
    }
}
