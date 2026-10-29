package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldHiddenWeaponsComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("combo","15_244"); dark.put("boba","5_091"); dark.put("effect","15_215");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo"));
        return scn;
    }
    private void attack(int destiny) {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var boba=scn.GetDSCard("boba"); var luke=scn.GetLSCard("luke");
        var site=scn.GetDSStartingLocation(); scn.MoveCardsToLocation(site,boba,luke);
        scn.EnsureDSForcePile(2); scn.SkipToDSTurn(Phase.BATTLE); scn.DSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareDSDestiny(destiny); scn.DSPlayCard(combo); scn.DSChooseCard(luke); scn.PassAllResponses();
        if(destiny==2 || destiny==3) {
            assertTrue(scn.DSCaptureDecisionAvailable()); scn.DSChooseSeizeCaptive();
            scn.PassAllResponses();
            if(scn.DSHasCardChoiceAvailable(boba)) { scn.DSChooseCard(boba); scn.PassAllResponses(); }
            assertTrue(luke.isCaptive()); assertEquals(boba,luke.getAttachedTo());
        }

        if(destiny==0) { assertTrue(luke.getZone().isInPlay()); assertFalse(luke.isHit()); }
        if(destiny==4) assertTrue(luke.isHit());
        if(destiny==6) assertTrue(scn.GetLSLostPile().contains(luke));
        assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void HiddenWeaponsAndTwilekAdvisorZeroDestinyHasNoEffectAndGoesUsed() { attack(0); }
    @Test public void HiddenWeaponsAndTwilekAdvisorTwoCapturesAndGoesUsed() { attack(2); }
    @Test public void HiddenWeaponsAndTwilekAdvisorThreeCapturesAndGoesUsed() { attack(3); }
    @Test public void HiddenWeaponsAndTwilekAdvisorFourDestinyHitsAndGoesUsed() { attack(4); }
    @Test public void HiddenWeaponsAndTwilekAdvisorSixDestinyLosesTargetAndGoesUsed() { attack(6); }
    @Test public void HiddenWeaponsAndTwilekAdvisorUploadsEffectForThreeForce() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var effect=scn.GetDSCard("effect");
        scn.EnsureDSForcePile(3); scn.MoveCardsToTopOfDSReserveDeck(effect); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(3); scn.MoveCardsToTopOfDSReserveDeck(effect);
        int force=scn.GetDSForcePileCount(); scn.DSPlayCard(combo,"Take card"); scn.PassAllResponses(); scn.DSChooseCard(effect); scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(effect)); assertEquals(force-3,scn.GetDSForcePileCount());
        assertTrue(scn.GetDSUsedPile().contains(combo));
    }
}
