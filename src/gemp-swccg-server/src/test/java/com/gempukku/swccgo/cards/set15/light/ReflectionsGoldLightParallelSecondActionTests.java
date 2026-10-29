package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelSecondActionTests {
    private void vision(boolean lost) {
        var light=new HashMap<String,String>(); light.put("card","15_111");
        var dark=new HashMap<String,String>(); dark.put("one","1_249"); dark.put("two","1_249");
        var s=new VirtualTableScenario(light,dark); var card=s.GetLSCard("card"); var one=s.GetDSCard("one"); var two=s.GetDSCard("two");
        s.StartGame(); s.MoveCardsToLSHand(card); s.SkipToLSTurn(Phase.CONTROL); s.MoveCardsToTopOfDSUsedPile(one,two); s.SkipToPhase(Phase.DEPLOY);
        s.EnsureDSForcePile(3); int force=s.GetDSForcePileCount();
        s.LSPlayCard(card,"Reveal opponent's Used Pile"); s.LSChooseOption(one.getTitle()); s.PassAllResponses(); s.LSDismissRevealedCards(); s.DSDismissRevealedCards(); s.PassAllResponses();
        s.LSChooseOption(lost?"Lost":"Used"); s.PassAllResponses();
        if(lost) { s.DSChooseCard(two); s.PassAllResponses(); assertTrue(s.GetDSLostPile().containsAll(List.of(one,two))); assertTrue(s.GetLSLostPile().contains(card)); }
        else { for(int i=0;i<2;i++) {s.DSChooseCard(s.GetTopOfDSForcePile());s.PassAllResponses();} assertEquals(force-2,s.GetDSForcePileCount());assertTrue(s.GetDSUsedPile().containsAll(List.of(one,two)));assertTrue(s.GetLSUsedPile().contains(card)); }
    }
    @Test public void VisoredVisionUsedLosesForceForEveryNamedCopy() {vision(false);}
    @Test public void VisoredVisionLostLosesEveryNamedCopy() {vision(true);}
    @Test public void GimmeALiftAddsLandspeedUntilEndOfTurnAsLost() {
        var light=new HashMap<String,String>();light.put("card","15_113");light.put("jarjar","11_4");
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var jar=s.GetLSCard("jarjar");s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(s.GetDSStartingLocation(),jar);s.SkipToLSTurn(Phase.CONTROL);
        int before=s.GetLandspeed(jar);s.LSPlayCard(card,"Add 1 to Jar Jar's landspeed");s.LSChooseCard(jar);s.PassAllResponses();assertEquals(before+1,s.GetLandspeed(jar));assertTrue(s.GetLSLostPile().contains(card));s.SkipToDSTurn(Phase.ACTIVATE);assertEquals(before,s.GetLandspeed(jar));
    }
    @Test public void GimmeALiftReturnsJarJarStackedOnBriskyMorningMunchen() {
        var light=new HashMap<String,String>();light.put("card","15_113");light.put("jarjar","11_4");light.put("brisky","11_16");
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var jar=s.GetLSCard("jarjar");s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLSSideOfTable(s.GetLSCard("brisky"));s.StackCardsOn(s.GetLSCard("brisky"),jar);s.SkipToLSTurn(Phase.CONTROL);
        s.LSPlayCard(card,"Take Jar Jar into hand");s.LSChooseCard(jar);s.PassAllResponses();assertTrue(s.GetLSHand().contains(jar));assertTrue(s.GetLSLostPile().contains(card));
    }
    private void edge(boolean success) {
        var light=new HashMap<String,String>();light.put("card","15_117");light.put("luke","1_19");
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var luke=s.GetLSCard("luke");s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(s.GetDSStartingLocation(),luke);s.SkipToLSTurn(Phase.CONTROL);s.EnsureLSForcePile(2);s.MoveCardsToTopOfLSLostPile(s.GetLSFiller(1),s.GetLSFiller(2));s.PrepareLSDestiny(success?3:2);int force=s.GetLSForcePileCount();
        s.LSPlayCard(card,"Target a Rebel");s.LSChooseCard(luke);s.PlayerDecided(s.LS,"2");s.PassAllResponses();
        if(success){s.LSChooseYes();s.PassAllResponses();assertFalse(s.GetLSLostPile().contains(luke));assertEquals(1,s.GetLSLostPileCount());}else assertTrue(s.GetLSLostPile().contains(luke));
        assertEquals(force-1,s.GetLSForcePileCount());assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void OnTheEdgeRetrievesChosenAmountOnHigherDestiny(){edge(true);}
    @Test public void OnTheEdgeLosesRebelOnEqualDestiny(){edge(false);}
    private void surpriseAssault(String id,boolean capped) {
        var light=new HashMap<String,String>();light.put("card",id);
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetDSFiller(1),s.GetDSFiller(2),s.GetDSFiller(3));s.SkipToDSTurn(Phase.CONTROL);s.EnsureLSForcePile(2);s.EnsureDSForcePile(5);
        assertEquals(3,s.GetPower(s.GetDSFiller(1))+s.GetPower(s.GetDSFiller(2))+s.GetPower(s.GetDSFiller(3)));
        s.PrepareLSDestiny(3);var third=s.GetTopOfLSReserveDeck();s.PrepareLSDestiny(2);s.PrepareLSDestiny(1);int dsLife=s.GetDSLifeForceRemaining();int lsForce=s.GetLSForcePileCount();
        s.DSForceDrainAt(site);s.DSPass();s.LSPlayCard(card,"Cancel Force drain");s.PassAllResponses();
        if(capped){assertTrue(s.GetLSReserveDeck().contains(third));assertEquals(dsLife,s.GetDSLifeForceRemaining());}
        else {for(int n=0;n<3;n++){s.DSChooseCard(s.GetTopOfDSForcePile());s.PassAllResponses();}assertFalse(s.GetLSReserveDeck().contains(third));assertEquals(dsLife-3,s.GetDSLifeForceRemaining());}
        assertEquals(lsForce-1,s.GetLSForcePileCount());assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void RemasteredSurpriseAssaultCapsDestinyAtTwoDespiteThreeCharacters(){surpriseAssault("15_117",true);}
    @Test public void OriginalSurpriseAssaultStillDrawsForAllThreeCharacters(){surpriseAssault("1_113",false);}
    @Test public void PodracePrepStartingModeDeploysArenaBothRacersEventAndFreeEffect() {
        var light=new HashMap<String,String>();light.put("card","15_121");light.put("arena","11_44");light.put("racer","11_47");light.put("boonta","11_24");light.put("effect","8_40");var dark=new HashMap<String,String>();dark.put("racer","11_98");
        var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");s.StartGame(false);s.LSChooseCard(card);s.PassAllResponses();s.LSChooseOption("Left");s.PassAllResponses();s.DSChooseCard(s.GetDSCard("racer"));s.PassAllResponses();
        assertEquals(Zone.LOCATIONS,s.GetLSCard("arena").getZone());assertEquals(s.GetLSCard("arena"),s.GetLSCard("racer").getAtLocation());assertEquals(s.GetLSCard("arena"),s.GetDSCard("racer").getAtLocation());assertEquals(s.GetLSCard("arena"),s.GetLSCard("boonta").getAttachedTo());assertEquals(Zone.SIDE_OF_TABLE,s.GetLSCard("effect").getZone());assertTrue(s.GetLSReserveDeck().contains(card)||s.GetLSHand().contains(card));assertEquals(8,s.GetLSHandCount());
    }
    @Test public void ThrowMeAnotherChargeUploadsAutomatedWeaponAsUsed() {
        var light=new HashMap<String,String>();light.put("card","15_124");light.put("charge","8_90");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var charge=s.GetLSCard("charge");s.StartGame();s.MoveCardsToLSHand(card);s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToTopOfLSReserveDeck(charge);
        s.LSPlayCard(card,"Take automated weapon into hand from Reserve Deck");s.PassAllResponses();s.LSChooseCard(charge);s.PassAllResponses();assertTrue(s.GetLSHand().contains(charge));assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void RebelLeadershipOriginalForfeitModeRemainsWhileRemasterOmitsIt() {
        var light=new HashMap<String,String>();light.put("card","15_124");light.put("original","9_54");light.put("general","9_13");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var original=s.GetLSCard("original");var general=s.GetLSCard("general");s.StartGame();s.MoveCardsToLSHand(card,original);s.MoveCardsToLocation(s.GetDSStartingLocation(),general);s.SkipToLSTurn(Phase.CONTROL);int before=s.GetForfeit(general);
        assertFalse(s.LSCardPlayAvailable(card,"Make admirals and generals forfeit +1"));s.LSPlayCard(original,"Make admirals and generals forfeit +1");s.PassAllResponses();assertEquals(before+1,s.GetForfeit(general));assertTrue(s.GetLSUsedPile().contains(original));
    }
    @Test public void RemasteredSurpriseAssaultCannotCancelOwnForceDrain() {
        var light=new HashMap<String,String>();light.put("card","15_117");light.put("luke","1_19");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("luke"));s.SkipToLSTurn(Phase.CONTROL);s.EnsureLSForcePile(2);s.LSForceDrainAt(site);assertTrue(s.LSGetDecision()==null||!s.LSCardPlayAvailable(card,"Cancel Force drain"));
    }
}
