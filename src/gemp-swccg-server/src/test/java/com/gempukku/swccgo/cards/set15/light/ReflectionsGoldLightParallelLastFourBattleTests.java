package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelLastFourBattleTests {
    private void reach(VirtualTableScenario s,com.gempukku.swccgo.game.PhysicalCardImpl card,String mode) {
        for(int i=0;i<24;i++){if(s.LSGetDecision()!=null&&s.LSCardPlayAvailable(card,mode))return;if(s.LSGetDecision()!=null)s.LSPass();else if(s.DSGetDecision()!=null)s.DSPass();}fail("Missing response: "+mode);
    }
    @Test public void JediMindTrickCancelsDrainWithLukeAtAdjacentSite() {
        var light=new HashMap<String,String>();light.put("card","15_139");light.put("luke","1_19");light.put("adjacent","1_128");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveLocationToTable(s.GetLSCard("adjacent"));s.MoveCardsToLocation(s.GetLSCard("adjacent"),s.GetLSCard("luke"));s.MoveCardsToLocation(site,s.GetDSFiller(1));s.SkipToDSTurn(Phase.CONTROL);int before=s.GetLSLifeForceRemaining();
        s.DSForceDrainAt(site);reach(s,card,"Cancel Force drain");s.LSPlayCard(card,"Cancel Force drain");s.PassAllResponses();assertEquals(before+1,s.GetLSLifeForceRemaining());assertTrue(s.GetLSUsedPile().contains(card));
    }
    private void brainDead(boolean darkJedi) {
        var light=new HashMap<String,String>();light.put("card","15_149");light.put("jedi","12_10");var dark=new HashMap<String,String>();dark.put("enemy",darkJedi?"1_168":"1_194");var s=new VirtualTableScenario(light,dark,30,30,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);var card=s.GetLSCard("card");var jedi=s.GetLSCard("jedi");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,jedi,s.GetDSCard("enemy"));s.SkipToDSTurn(Phase.BATTLE);s.EnsureDSForcePile(2);s.DSInitiateBattle(site);s.PassAllResponses();s.DSPass();
        s.LSPlayCard(card,"Make a Jedi immune to attrition");s.LSChooseCard(jedi);s.PassAllResponses();assertEquals(!darkJedi,s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(),jedi)==Float.MAX_VALUE);assertTrue(s.GetLSUsedPile().contains(card));if(!darkJedi){s.SkipToDamageSegment(false);s.PassAllResponses();s.DSChooseCard(s.GetDSCard("enemy"));s.PassAllResponses();for(int i=0;i<6&&s.AwaitingDSBattleDamagePayment();i++){s.DSPayBattleDamageFromReserveDeck();s.PassAllResponses();}s.SkipToPhase(Phase.DRAW);s.PassDrawActions();s.PassAllResponses();assertTrue(s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(),jedi)<Float.MAX_VALUE);}
    }
    @Test public void BrainDeadMakesDefendingEpisodeIJediImmune(){brainDead(false);}
    @Test public void BrainDeadDoesNotGrantImmunityWithDarkJediPresent(){brainDead(true);}
    private void threepio(boolean ewoks) {
        var light=new HashMap<String,String>();light.put("card","15_150");light.put("droid","1_5");light.put("luke","1_19");light.put("chirpa","8_3");light.put("logray","8_20");light.put("wicket","8_32");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("droid"),s.GetLSCard("luke"),s.GetDSFiller(1));if(ewoks)s.MoveCardsToLocation(site,s.GetLSCard("chirpa"),s.GetLSCard("logray"),s.GetLSCard("wicket"));s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(2);s.LSInitiateBattle(site);s.PassAllResponses();int before=s.GetLSBattleDestinyCount();s.LSPlayCard(card,ewoks?"Add two battle destiny":"Add one battle destiny");s.PassAllResponses();assertEquals(before+(ewoks?2:1),s.GetLSBattleDestinyCount());assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void C3POAddsOneBattleDestinyAtSite(){threepio(false);}
    @Test public void C3POWithThreeEwoksAddsTwoBattleDestiny(){threepio(true);}
    @Test public void IKnowAddsTwoBattleDestinyWithHanAndLeia() {
        var light=new HashMap<String,String>();light.put("card","15_150");light.put("han","1_11");light.put("leia","1_17");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("han"),s.GetLSCard("leia"),s.GetDSFiller(1));s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(2);s.LSInitiateBattle(site);s.PassAllResponses();int before=s.GetLSBattleDestinyCount();s.LSPlayCard(card,"Add two battle destiny");s.PassAllResponses();assertEquals(before+2,s.GetLSBattleDestinyCount());assertTrue(s.GetLSLostPile().contains(card));
    }
    private void react(boolean fromReserve) {
        var light=new HashMap<String,String>();light.put("card","15_150");light.put("han","1_11");light.put("leia","1_17");light.put("weapon","1_156");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var leia=s.GetLSCard("leia");var weapon=s.GetLSCard("weapon");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("han"),s.GetDSFiller(1));s.SkipToDSTurn(Phase.BATTLE);s.EnsureDSForcePile(2);if(fromReserve)s.MoveCardsToTopOfLSReserveDeck(leia,weapon);else s.MoveCardsToLSHand(leia,weapon);int force=s.GetLSForcePileCount();s.DSInitiateBattle(site);reach(s,card,"Deploy Leia");s.LSPlayCard(card,"Deploy Leia");s.PassAllResponses();
        s.LSChooseCard(leia);s.PassAllResponses();s.LSChooseCard(weapon);s.PassAllResponses();assertEquals(site,leia.getAtLocation());assertEquals(leia,weapon.getAttachedTo());assertEquals(force,s.GetLSForcePileCount());assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void IKnowReactsLeiaAndMatchingWeaponFromHandForFree(){react(false);}
    @Test public void IKnowReactsLeiaAndMatchingWeaponFromReserveForFree(){react(true);}
    @Test public void StrangleAddsBattleDestinyWithEscortedLeiaAtSite() {
        var light=new HashMap<String,String>();light.put("card","15_154");light.put("luke","1_19");light.put("leia","1_17");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSFiller(1));s.CaptureCardWith(s.GetDSFiller(1),s.GetLSCard("leia"));s.SkipToLSTurn(Phase.BATTLE);s.EnsureLSForcePile(2);s.LSInitiateBattle(site);s.PassAllResponses();int before=s.GetLSBattleDestinyCount();s.LSPlayCard(card,"Add one battle destiny");s.PassAllResponses();assertEquals(before+1,s.GetLSBattleDestinyCount());assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void BrainDeadReturnsForfeitedCouncilMemberAndCancelsDamage() {
        var light=new HashMap<String,String>();light.put("card","15_149");light.put("jedi","12_10");var dark=new HashMap<String,String>();dark.put("vader","1_168");var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var jedi=s.GetLSCard("jedi");var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(site,jedi,s.GetDSCard("vader"),s.GetDSFiller(1),s.GetDSFiller(2),s.GetDSFiller(3),s.GetDSFiller(4));s.SkipToDSTurn(Phase.BATTLE);s.EnsureDSForcePile(2);s.DSInitiateBattle(site);s.PassAllResponses();s.PrepareDSDestiny(6);s.PrepareLSDestiny(0);s.SkipToDamageSegment(true);s.LSChooseCard(jedi);reach(s,card,"Place Ki-Adi-Mundi");s.LSPlayCard(card);s.PassAllResponses();assertTrue(s.GetLSUsedPile().contains(jedi));assertTrue(s.GetLSLostPile().contains(card));assertFalse(com.gempukku.swccgo.cards.GameConditions.isBattleDamageRemaining(s.game(),s.LS));assertFalse(com.gempukku.swccgo.cards.GameConditions.isAttritionRemaining(s.game(),s.LS));
    }
}
