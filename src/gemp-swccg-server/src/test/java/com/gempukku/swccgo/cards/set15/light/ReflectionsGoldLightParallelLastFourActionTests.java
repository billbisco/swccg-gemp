package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelLastFourActionTests {
    private void upload(String id,String targetId,String action,int cost) {
        var light=new HashMap<String,String>();light.put("card",id);light.put("target",targetId);
        var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var target=s.GetLSCard("target");s.StartGame();s.MoveCardsToLSHand(card);s.SkipToLSTurn(Phase.CONTROL);s.EnsureLSForcePile(cost+1);s.MoveCardsToTopOfLSReserveDeck(target);int before=s.GetLSForcePileCount();
        s.LSPlayCard(card,action);s.PassAllResponses();if(!s.GetLSHand().contains(target)){s.LSChooseCard(target);s.PassAllResponses();}assertTrue(s.GetLSHand().contains(target));assertTrue(s.GetLSUsedPile().contains(card));assertEquals(before-cost,s.GetLSForcePileCount());
    }
    @Test public void SignalUploadsEffectForThreeForce(){upload("15_139","8_40","Take card into hand from Reserve Deck",3);}
    @Test public void WeWishToBoardUploadsEffectForThreeForce(){upload("15_149","8_40","Take Effect into hand from Reserve Deck",3);}
    @Test public void WeWishToBoardUploadsRadiantVIIForFree(){upload("15_149","12_92","Take Radiant VII or Interrupt into hand from Reserve Deck",0);}
    @Test public void WeWishToBoardUploadsPodracerInterruptForFree(){upload("15_149","11_40","Take Radiant VII or Interrupt into hand from Reserve Deck",0);}
    @Test public void WereYouLookingForMeRetrievesC3POAsLost() {
        var light=new HashMap<String,String>();light.put("card","15_150");light.put("threepio","1_5");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var droid=s.GetLSCard("threepio");s.StartGame();s.MoveCardsToLSHand(card);s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToTopOfLSLostPile(droid);s.LSPass();s.DSPass();
        s.LSPlayCard(card,"Retrieve C-3PO");s.PassAllResponses();s.LSChooseCard(droid);s.PassAllResponses();assertTrue(s.GetLSUsedPile().contains(droid));assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void SignalRequiresFullThreeForcePayment() {
        var light=new HashMap<String,String>();light.put("card","15_139");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");s.StartGame();s.MoveCardsToLSHand(card);s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToTopOfLSReserveDeck(s.GetLSForcePile().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));s.EnsureLSForcePile(2);s.LSPass();s.DSPass();assertFalse(s.LSCardPlayAvailable(card,"Take card into hand from Reserve Deck"));
    }
    @Test public void TakeMeToJabbaRelocatesCharacterFromRelatedSiteAsUsed() {
        var light=new HashMap<String,String>();light.put("card","15_154");light.put("luke","1_19");var dark=new HashMap<String,String>();dark.put("jabba","6_109");dark.put("chamber","6_162");var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var luke=s.GetLSCard("luke");var chamber=s.GetDSCard("chamber");s.StartGame();s.MoveLocationToTable(chamber);s.MoveCardsToLSHand(card);s.MoveCardsToLocation(s.GetDSStartingLocation(),luke);s.MoveCardsToLocation(chamber,s.GetDSCard("jabba"));s.SkipToLSTurn(Phase.CONTROL);
        s.LSPlayCard(card,"Relocate character to Jabba's site");s.LSChooseCard(luke);s.PassAllResponses();assertEquals(chamber,luke.getAtLocation());assertTrue(s.GetLSUsedPile().contains(card));
    }
    private void strangle(boolean success) {
        var light=new HashMap<String,String>();light.put("card","15_154");light.put("captive","1_28");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var captive=s.GetLSCard("captive");var escort=s.GetDSFiller(1);s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToLocation(s.GetDSStartingLocation(),escort);s.CaptureCardWith(escort,captive);s.SkipToLSTurn(Phase.CONTROL);s.PrepareLSDestiny(success?6:0);
        s.LSPlayCard(card,"Target an escort alone");s.LSChooseCard(escort);s.LSChooseCard(captive);s.PassAllResponses();if(success){s.LSChooseOption("Escape");s.PassAllResponses();}assertEquals("decision="+s.GetCurrentDecision().getText()+" escort zone="+escort.getZone()+" captive power="+s.GetPower(captive)+" escort power="+s.GetPower(escort)+" ability="+s.GetAbility(escort),success,s.GetDSLostPile().contains(escort));assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void StrangleLosesEscortWhenDestinyPlusPowerExceedsDefense(){strangle(true);}
    @Test public void StrangleDoesNotLoseEscortOnInsufficientDestiny(){strangle(false);}
    @Test public void WereYouLookingUploadsTwoProtocolDroidsAsUsed() {
        var light=new HashMap<String,String>();light.put("card","15_150");light.put("first","1_5");light.put("second","3_14");var s=new VirtualTableScenario(light,new HashMap<>());var card=s.GetLSCard("card");var first=s.GetLSCard("first");var second=s.GetLSCard("second");s.StartGame();s.MoveCardsToLSHand(card);s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToTopOfLSReserveDeck(first,second);
        s.LSPlayCard(card,"Take cards into hand from Reserve Deck");s.PassAllResponses();s.LSChooseCard(first);s.PassAllResponses();s.LSChooseCard(second);s.PassAllResponses();assertTrue(s.GetLSHand().containsAll(List.of(first,second)));assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void TakeMeToJabbaDeploysAlienToAudienceChamberAtNormalCost() {
        var light=new HashMap<String,String>();light.put("card","15_154");light.put("alien","1_20");var dark=new HashMap<String,String>();dark.put("chamber","6_162");var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var alien=s.GetLSCard("alien");var chamber=s.GetDSCard("chamber");s.StartGame();s.MoveLocationToTable(chamber);s.MoveCardsToLSHand(card);s.SkipToLSTurn(Phase.DEPLOY);s.EnsureLSForcePile(5);s.MoveCardsToTopOfLSReserveDeck(alien);int before=s.GetLSForcePileCount();
        s.LSPlayCard(card,"Deploy alien from Reserve Deck");s.PassAllResponses();s.LSChooseCard(alien);s.PassAllResponses();assertEquals(chamber,alien.getAtLocation());assertEquals(before-alien.getBlueprint().getDeployCost().intValue(),s.GetLSForcePileCount());assertTrue(s.GetLSUsedPile().contains(card));
    }
}
