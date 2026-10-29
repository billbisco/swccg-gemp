package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;

import com.gempukku.swccgo.framework.*;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ReflectionsGoldLandingDroidsTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>();
        light.put("luke","1_19");light.put("goo","7_062");
        var dark=new HashMap<String,String>();
        dark.put("combo","15_174");
        dark.put("flagship","14_114");
        dark.put("system","12_169");
        dark.put("site","12_170");
        dark.put("bay","12_173");
        dark.put("droid","12_118");
        dark.put("lott","12_110");
        dark.put("iao","12_135");

        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        s.StartGame();
        s.MoveLocationToTable(s.GetDSCard("system"));
        s.MoveLocationToTable(s.GetDSCard("site"));
        s.MoveCardsToLocation(s.GetDSCard("system"),s.GetDSCard("flagship"));
        s.MoveCardsToDSHand(s.GetDSCard("combo"));
        s.SkipToDSTurn(Phase.DEPLOY);
        s.DSPlayCard(s.GetDSCard("combo"));
        s.DSChooseCard(s.GetDSCard("flagship"));
        s.PassAllResponses();
        return s;

    }
    @Test public void LandingDroidsAddsTwoForfeitToUniqueRepublicCharacter() {var s=scenario();
        var lott=s.GetDSCard("lott");
        s.MoveCardsToLocation(s.GetDSStartingLocation(),lott);
        assertEquals(lott.getBlueprint().getForfeit()+2,s.GetForfeit(lott),s.epsilon);
        }
    @Test public void LandingDroidsDownloadsDockingBayOncePerDeploy() {
        var s=scenario();
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSCard("bay"));
        if(s.DSGetDecision()==null)s.LSPass(); s.DSUseCardAction(s.GetDSCard("combo"),"Deploy docking bay"); s.DSChooseCard(s.GetDSCard("bay"));s.PassAllResponses();s.DSChooseOption("Left");
        s.PassAllResponses();
        assertTrue(s.GetDSCard("bay").getZone().isInPlay());
        if(s.DSGetDecision()==null)s.LSPass();assertFalse(s.DSCardActionAvailable(s.GetDSCard("combo"),"Deploy docking bay"));

    }
    private void battle(String option) {
        var s=scenario();
        var combo=s.GetDSCard("combo");
        s.MoveCardsToLocation(s.GetDSCard("site"),s.GetLSCard("luke"),s.GetDSCard("droid"));
        s.MoveCardsToTopOfDSLostPile(s.GetDSFiller(1));
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(s.GetDSCard("site"));
        int force=s.GetDSForcePileCount();

        for(int i=0;
        i<12&&(s.DSGetDecision()==null||!s.DSCardActionAvailable(combo,"Activate 2 Force"));
        i++) {if(s.LSGetDecision()!=null&&s.LSDecisionAvailable("Optional"))s.LSPass();
        else if(s.DSGetDecision()!=null&&s.DSDecisionAvailable("Optional"))s.DSPass();
        else break;
        }
        assertTrue(s.DSCardActionAvailable(combo,"Activate 2 Force"));

        if(option.equals("decline")) {s.DSPass();
        s.PassAllResponses();
        assertEquals(force,s.GetDSForcePileCount());
        assertEquals(1,s.GetDSLostPileCount());
        }
        else {s.DSUseCardAction(combo,"Activate 2 Force");
        s.DSChooseOption(option);
        s.PassAllResponses();
        assertEquals(force+(option.startsWith("Activate")?2:0),s.GetDSForcePileCount());
        assertEquals(option.startsWith("Retrieve")?0:1,s.GetDSLostPileCount());
        }
    }
    @Test public void DroidsBattleMayActivateTwo() {battle("Activate");
        }
    @Test public void DroidsBattleMayRetrieveOne() {battle("Retrieve");
        }
    @Test public void DroidsBattleMayDeclineBothRewards() {battle("decline");
        }
    @Test public void ImperialArrestOrderBlocksDockingBayDownload() {
        var s=scenario();
        s.MoveCardsToDSSideOfTable(s.GetDSCard("iao"));
        s.SkipToPhase(Phase.DRAW);
        s.SkipToDSTurn(Phase.DEPLOY);
        assertNotNull(s.DSGetDecision());
        if(s.DSGetDecision()==null)s.LSPass();assertFalse(s.DSCardActionAvailable(s.GetDSCard("combo"),"Deploy docking bay"));

    }    private void gooNeeTay(boolean landingPresent) {
        var s=scenario();if(!landingPresent)s.MoveCardsToDSHand(s.GetDSCard("combo"));s.MoveCardsToLSSideOfTable(s.GetLSCard("goo"));s.MoveCardsToDSHand(s.GetDSCard("lott"));s.SkipToPhase(Phase.DRAW);s.SkipToDSTurn(Phase.DEPLOY);s.EnsureDSForcePile(8);int before=s.GetDSForcePileCount();s.DSPlayCard(s.GetDSCard("lott"));s.DSChooseCard(s.GetDSStartingLocation());s.PassAllResponses();assertEquals(landingPresent?2:4,before-s.GetDSForcePileCount());assertTrue(s.GetDSCard("lott").getZone().isInPlay());
    }
    @Test public void LandingProtectsUniqueRepublicCharacterFromGooNeeTayCost() {gooNeeTay(true);}
    @Test public void GooNeeTayRaisesSameCharactersCostWithoutLanding() {gooNeeTay(false);}}
