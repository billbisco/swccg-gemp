package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import com.gempukku.swccgo.filters.Filters;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelSecondResponseTests {
    private void reach(VirtualTableScenario s,com.gempukku.swccgo.game.PhysicalCardImpl card,String mode) {
        for(int i=0;i<24;i++) {if(s.LSGetDecision()!=null&&s.LSCardPlayAvailable(card,mode))return;if(s.LSGetDecision()!=null)s.LSPass();else if(s.DSGetDecision()!=null)s.DSPass();} fail("Missing response: "+mode);
    }
    @Test public void NiceOfYouGuysRecoversMissingCharacterAtPilotedVehicleSite() {
        var light=new HashMap<String,String>();light.put("card","15_111");light.put("luke","1_19");light.put("speeder","3_69");
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var luke=s.GetLSCard("luke");s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(s.GetDSStartingLocation(),luke,s.GetLSCard("speeder"));luke.setMissing(true);s.SkipToLSTurn(Phase.CONTROL);
        s.LSPlayCard(card,"Send missing character to Used Pile");s.LSChooseCard(luke);s.PassAllResponses();assertTrue(s.GetLSUsedPile().contains(luke));assertTrue(s.GetLSUsedPile().contains(card));assertFalse(luke.isMissing());
    }
    @Test public void NiceOfYouGuysCancelsRetwinsActualHideAttempt() {
        var light=new HashMap<String,String>();light.put("card","15_111");light.put("luke","1_19");var dark=new HashMap<String,String>();dark.put("retwin","5_93");
        var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var retwin=s.GetDSCard("retwin");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("luke"),retwin);s.SkipToDSTurn(Phase.BATTLE);s.EnsureDSForcePile(5);s.DSInitiateBattle(site);s.PassAllResponses();
        s.DSUseCardAction(retwin,"'Hide' from battle");reach(s,card,"Cancel attempt to 'hide'");s.LSPlayCard(card);s.PassAllResponses();assertTrue(Filters.participatingInBattle.accepts(s.game(),retwin));assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void NoGibenAddsTwoBattleDestinyOnlyWithBothGungansParticipating() {
        var light=new HashMap<String,String>();light.put("card","15_113");light.put("jarjar","11_4");light.put("tarpals","14_7");
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("jarjar"),s.GetLSCard("tarpals"),s.GetDSFiller(1));s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(2);s.LSInitiateBattle(site);s.PassAllResponses();int before=s.GetLSBattleDestinyCount();
        s.LSPlayCard(card,"Add two battle destiny");s.PassAllResponses();assertEquals(before+2,s.GetLSBattleDestinyCount());assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void GimmeALiftRelocatesJarJarFromAdjacentSiteIntoNewBattle() {
        var light=new HashMap<String,String>();light.put("card","15_113");light.put("jarjar","11_4");light.put("luke","1_19");light.put("adjacent","1_128");
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var jar=s.GetLSCard("jarjar");var site=s.GetDSStartingLocation();s.StartGame();s.MoveLocationToTable(s.GetLSCard("adjacent"));s.MoveCardsToLSHand(card);s.MoveCardsToLocation(s.GetLSCard("adjacent"),jar);s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSFiller(1));s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(2);s.LSInitiateBattle(site);reach(s,card,"Relocate Jar Jar");
        s.LSPlayCard(card,"Relocate Jar Jar");s.LSChooseCard(jar);s.PassAllResponses();assertEquals(site,jar.getAtLocation());assertTrue(Filters.participatingInBattle.accepts(s.game(),jar));assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void NoGibenReturnsGunganActuallyForfeitedFromBattle() {
        var light=new HashMap<String,String>();light.put("card","15_113");light.put("jarjar","11_4");var dark=new HashMap<String,String>();dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var jar=s.GetLSCard("jarjar");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,jar,s.GetDSCard("vader"));s.SkipToDSTurn(Phase.BATTLE);s.EnsureDSForcePile(2);s.DSInitiateBattle(site);s.SkipToDamageSegment(false);s.LSChooseCard(jar);reach(s,card,"Place Jar Jar");s.LSPlayCard(card);s.PassAllResponses();assertTrue(s.GetLSHand().contains(jar));assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void SlightWeaponsMalfunctionAddsDestinyAfterActualBlasterFire() {
        var light=new HashMap<String,String>();light.put("card","15_127");light.put("luke","1_19");light.put("blaster","1_152");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var blaster=s.GetLSCard("blaster");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSFiller(1));s.AttachCardsTo(s.GetLSCard("luke"),blaster);s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(3);s.LSStartBattleAndSkipToWeaponsSegment(site);int before=s.GetLSBattleDestinyCount();s.PrepareLSDestiny(0);
        s.LSUseCardAction(blaster);s.LSChooseCard(s.GetDSFiller(1));reach(s,card,"Add one battle destiny");s.LSPlayCard(card,"Add one battle destiny");s.PassAllResponses();assertEquals(before+1,s.GetLSBattleDestinyCount());assertTrue(s.GetLSUsedPile().contains(card));
    }
    private void leadership(boolean limit) {
        var light=new HashMap<String,String>();light.put("card","15_124");light.put("general","9_13");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("general"),s.GetDSFiller(1));s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(2);s.LSInitiateBattle(site);s.PassAllResponses();int before=s.GetLSBattleDestinyCount();
        s.LSPlayCard(card,limit?"Limit opponent to one battle destiny":"Add one battle destiny");s.PassAllResponses();if(limit)assertEquals(1,s.game().getModifiersQuerying().getNumBattleDestinyDraws(s.gameState(),s.DS,true,false));else assertEquals(before+1,s.GetLSBattleDestinyCount());assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void RebelLeadershipAddsDestinyWithParticipatingGeneral(){leadership(false);}
    @Test public void RebelLeadershipCapsOpponentsBattleDestinyAtOne(){leadership(true);}
    private void cancelRaceInterrupt(boolean collision) {
        var light=new HashMap<String,String>();light.put("card","15_121");light.put("racer","11_47");light.put("arena","11_44");light.put("boonta","11_24");var dark=new HashMap<String,String>();dark.put("attack",collision?"11_85":"11_81");dark.put("racer","11_98");
        var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var racer=s.GetLSCard("racer");var attack=s.GetDSCard("attack");s.StartGame();s.LSChooseCards();s.PassAllResponses();s.MoveCardsToLSHand(card);s.MoveCardsToDSHand(attack);s.SkipToDSTurn(Phase.ACTIVATE);s.MoveLocationToTable(s.GetLSCard("arena"));s.MoveCardsToLocation(s.GetLSCard("arena"),racer,s.GetDSCard("racer"));s.AttachCardsTo(s.GetLSCard("arena"),s.GetLSCard("boonta"));s.gameState().setPodraceStarted(s.GetLSCard("boonta"));s.EnsureDSForcePile(3);var destiny=s.GetLSFiller(1);s.StackCardsOn(racer,destiny);destiny.setRaceDestinyForPlayer(s.LS);s.SkipToPhase(Phase.CONTROL);
        s.DSPlayCard(attack,collision?"Return a race destiny to hand":"Target a Podracer");s.DSChooseCard(racer);if(collision){s.PassAllResponses();s.DSChooseCard(s.GetTopOfDSForcePile());}
        reach(s,card,"Cancel");s.LSPlayCard(card);s.PassAllResponses();assertTrue(s.GetLSLostPile().contains(card));assertTrue(s.GetDSLostPile().contains(attack));assertTrue(racer.getCardsStacked().contains(destiny));
    }
    @Test public void TooCloseForComfortCancelsHitRacerBeingPlayed(){cancelRaceInterrupt(false);}
    @Test public void TooCloseForComfortCancelsPodracerCollisionBeingPlayed(){cancelRaceInterrupt(true);}
    @Test public void PodraceCombinationOmitsOriginalWeaponTargetingTax() {
        var light=new HashMap<String,String>();light.put("card","15_121");light.put("original","11_40");light.put("luke","1_19");var dark=new HashMap<String,String>();dark.put("blaster","1_317");var s=new VirtualTableScenario(light,dark,30,30,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);var card=s.GetLSCard("card");var original=s.GetLSCard("original");var site=s.GetDSStartingLocation();s.StartGame();s.LSChooseCards();s.PassAllResponses();s.MoveCardsToLSHand(card,original);s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSFiller(1));s.AttachCardsTo(s.GetDSFiller(1),s.GetDSCard("blaster"));s.SkipToDSTurn(Phase.BATTLE);s.EnsureDSForcePile(3);s.DSStartBattleAndSkipToWeaponsSegment(site);s.DSUseCardAction(s.GetDSCard("blaster"));s.DSChooseCard(s.GetLSCard("luke"));reach(s,original,"Make opponent use 2 Force");assertTrue(s.LSCardPlayAvailable(original,"Make opponent use 2 Force"));assertFalse(s.LSCardPlayAvailable(card,"Make opponent use 2 Force"));
    }
    @Test public void RebelLeadershipCannotUseGeneralOutsideBattle() {
        var light=new HashMap<String,String>();light.put("card","15_124");light.put("general","9_13");light.put("luke","1_19");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("general"));s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSFiller(1));s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(2);s.LSInitiateBattle(site);s.PassAllResponses();assertFalse(s.LSCardPlayAvailable(card,"Add one battle destiny"));assertFalse(s.LSCardPlayAvailable(card,"Limit opponent to one battle destiny"));
    }
}
