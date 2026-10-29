package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class ReflectionsGoldKraytJusticeTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>();light.put("nabrun","1_97");light.put("luke","1_19");
        var dark=new HashMap<String,String>();dark.put("combo","15_191");dark.put("trooper","1_194");
        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        s.StartGame();s.MoveCardsToDSHand(s.GetDSCard("combo"));s.SkipToDSTurn(Phase.DEPLOY);
        s.DSPlayCard(s.GetDSCard("combo"),"immunity");s.DSChooseCard(s.GetDSStartingLocation());s.PassAllResponses();return s;
    }
    private void destiny(int value) {
        var s=scenario();var effect=s.GetDSCard("combo");var used=s.GetDSFiller(1);var lost=s.GetDSFiller(2);var trooper=s.GetDSCard("trooper");
        s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("luke"));s.MoveCardsToLSHand(s.GetLSCard("nabrun"));s.SkipToLSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfDSUsedPile(used);s.MoveCardsToTopOfDSLostPile(lost);s.MoveCardsToDSHand(trooper);s.EnsureDSForcePile(2);s.MoveCardsToTopOfDSReserveDeck(s.GetDSFiller(3));
        int force=s.GetDSForcePileCount();int losses=s.GetDSLostPileCount();s.PrepareLSDestiny(value);
        s.LSPlayCard(s.GetLSCard("nabrun"));s.LSChooseCard(s.GetLSStartingLocation());s.LSChooseCard(s.GetDSStartingLocation());s.LSChooseCard(s.GetLSCard("luke"));s.PassAllResponses();
        if(value==0) assertTrue(s.GetDSHand().contains(used));
        if(value==1) {assertTrue(s.AwaitingDSForceLossPayment());s.DSPayRemainingForceLossFromReserveDeck();s.PassAllResponses();assertEquals(losses+1,s.GetDSLostPileCount());}
        if(value==2) assertEquals(force+1,s.GetDSForcePileCount());
        if(value==3) assertTrue(s.GetDSLostPile().contains(effect));
        if(value==4) assertTrue(s.GetDSUsedPile().contains(lost));
        if(value==5) {assertTrue("location choice: "+s.DSGetDecision(),s.DSHasCardChoiceAvailable(s.GetDSStartingLocation()));s.DSChooseCard(s.GetDSStartingLocation());s.PassAllResponses();assertEquals(force,s.GetDSForcePileCount());assertTrue(trooper.getZone().isInPlay());}
        if(value==6) {assertEquals(force,s.GetDSForcePileCount());assertEquals(losses,s.GetDSLostPileCount());assertTrue(effect.getZone().isInPlay());}
    }
    @Test public void DestinyZeroDrawsTopUsedCard() {destiny(0);}
    @Test public void DestinyOneLosesOneForce() {destiny(1);}
    @Test public void DestinyTwoActivatesOneForce() {destiny(2);}
    @Test public void DestinyThreeLosesEffect() {destiny(3);}
    @Test public void DestinyFourRetrievesOneForce() {destiny(4);}
    @Test public void DestinyFiveDeploysCharacterFree() {destiny(5);}
    @Test public void DestinySixHasNoExtraEffect() {destiny(6);}
    @Test public void RevolutionImmunityModeDoesNotCancelForceGeneration() {
        var s=scenario();var site=s.GetDSStartingLocation();var q=s.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(s.gameState(),site,Title.Revolution));
        assertTrue(q.getForceGenerationFromLocation(s.gameState(),site,s.DS)>0);
    }
    @Test public void ImperialJusticeGenerationModeRequiresFourExtraAndCancelsBothSides() {
        var light=new HashMap<String,String>();light.put("obiHut","1_134");light.put("yodaHut","4_089");var dark=new HashMap<String,String>();dark.put("combo","15_191");var s=new VirtualTableScenario(light,dark);s.StartGame();s.MoveCardsToDSHand(s.GetDSCard("combo"));s.MoveLocationToTable(s.GetLSCard("obiHut"));s.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(s.DSCardPlayAvailable(s.GetDSCard("combo"),"immunity"));assertFalse(s.DSCardPlayAvailable(s.GetDSCard("combo"),"cancel Force generation"));s.MoveLocationToTable(s.GetLSCard("yodaHut"));s.SkipToPhase(Phase.DRAW);s.SkipToDSTurn(Phase.DEPLOY);assertTrue(s.DSCardPlayAvailable(s.GetDSCard("combo"),"cancel Force generation"));
        s.DSPlayCard(s.GetDSCard("combo"),"cancel Force generation");s.DSChooseCard(s.GetDSStartingLocation());s.PassAllResponses();var q=s.game().getModifiersQuerying();assertEquals(0,q.getForceGenerationFromLocation(s.gameState(),s.GetDSStartingLocation(),s.DS),s.epsilon);assertEquals(0,q.getForceGenerationFromLocation(s.gameState(),s.GetDSStartingLocation(),s.LS),s.epsilon);assertFalse(q.isImmuneToCardTitle(s.gameState(),s.GetDSStartingLocation(),Title.Revolution));
    }}
