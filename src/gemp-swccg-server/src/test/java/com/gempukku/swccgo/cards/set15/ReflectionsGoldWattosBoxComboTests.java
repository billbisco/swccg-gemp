package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class ReflectionsGoldWattosBoxComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>();light.put("luke","1_19");light.put("saber","1_155");
        var dark=new HashMap<String,String>();dark.put("combo","15_213");dark.put("aurra","11_051");
        var s=new VirtualTableScenario(light,dark);s.StartGame();s.MoveCardsToDSHand(s.GetDSCard("combo"));s.SkipToDSTurn(Phase.DEPLOY);s.DSPlayCardAndPassResponses(s.GetDSCard("combo"));return s;
    }
    @Test public void WattosBoxAndQuietlyObservingAddsFivePodraceRewardsAndIsImmuneToAlter() {
        var s=scenario();var q=s.game().getModifiersQuerying();s.gameState().setPodraceStarted(s.GetDSCard("combo"));s.gameState().setPodraceWinner(s.DS);s.gameState().setPodraceLoser(s.LS);
        assertEquals(11f,q.getPodraceForceRetrieval(s.gameState(),6),s.epsilon);assertEquals(11f,q.getPodraceForceLoss(s.gameState(),6),s.epsilon);
        assertTrue(q.isImmuneToCardTitle(s.gameState(),s.GetDSCard("combo"),Title.Alter));
    }
    @Test public void WattosBoxAndQuietlyObservingZeroesOnlyAloneAurrasSiteAbility() {
        var s=scenario();var site=s.GetDSStartingLocation();s.MoveCardsToLocation(site,s.GetDSCard("aurra"));
        assertEquals(0,s.GetDSAbilityAtLocation(site));s.MoveCardsToLocation(site,s.GetDSFiller(1));assertTrue(s.GetDSAbilityAtLocation(site)>0);
        s.MoveCardsToDSHand(s.GetDSFiller(1));assertEquals(0,s.GetDSAbilityAtLocation(site));
    }
    private void steal(int destiny,boolean yes) {
        var s=scenario();var combo=s.GetDSCard("combo");var aurra=s.GetDSCard("aurra");var luke=s.GetLSCard("luke");var saber=s.GetLSCard("saber");
        s.MoveCardsToLocation(s.GetDSStartingLocation(),aurra,luke);s.AttachCardsTo(luke,saber);s.SkipToPhase(Phase.MOVE);s.PrepareDSDestiny(destiny);
        s.DSUseCardAction(combo,"'Steal'");s.DSChooseCard(saber);s.PassAllResponses();
        if(destiny>3) {if(yes)s.DSChooseYes();else s.DSChooseNo();s.PassAllResponses();}
        assertTrue(s.GetDSLostPile().contains(combo));assertEquals(destiny>3&&yes?aurra:luke,saber.getAttachedTo());
    }
    @Test public void WattosBoxAndQuietlyObservingDestinyFourStealsLightsaber() {steal(4,true);}
    @Test public void WattosBoxAndQuietlyObservingDestinyThreeDoesNotSteal() {steal(3,true);}
    @Test public void WattosBoxAndQuietlyObservingCanDeclineSuccessfulTheft() {steal(4,false);}
    @Test public void WattosBoxCannotDeployAfterOwnerHasNonzeroRaceTotal() {
        var dark=new HashMap<String,String>();dark.put("combo","15_213");dark.put("arena","11_094");dark.put("trooper","1_194");var s=new VirtualTableScenario(new HashMap<>(),dark);s.StartGame();s.MoveLocationToTable(s.GetDSCard("arena"));s.MoveCardsToDSHand(s.GetDSCard("combo"),s.GetDSCard("trooper"));var destiny=s.GetDSDestiny(1);s.StackCardsOn(s.GetDSCard("arena"),destiny);destiny.setRaceDestinyForPlayer(s.DS);s.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(1,s.game().getModifiersQuerying().getHighestRaceTotal(s.gameState(),s.DS),s.epsilon);assertTrue(s.DSCardPlayAvailable(s.GetDSCard("trooper")));assertFalse(s.DSCardPlayAvailable(s.GetDSCard("combo")));
    }}
