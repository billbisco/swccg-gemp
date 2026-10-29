package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class ReflectionsGoldScumInformationTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>();light.put("luke","1_19");
        var dark=new HashMap<String,String>();dark.put("combo","15_206");dark.put("broker","6_114");dark.put("alien","6_091");dark.put("trooper","1_194");dark.put("chamber","6_162");dark.put("dungeon","6_164");dark.put("cavern","6_165");
        var s=new VirtualTableScenario(light,dark);s.StartGame();s.MoveLocationToTable(s.GetDSCard("chamber"));s.MoveLocationToTable(s.GetDSCard("dungeon"));s.MoveLocationToTable(s.GetDSCard("cavern"));s.MoveCardsToDSHand(s.GetDSCard("combo"),s.GetDSCard("alien"));s.MoveCardsToLocation(s.GetDSCard("chamber"),s.GetDSCard("broker"));s.SkipToDSTurn(Phase.DEPLOY);s.DSPlayCard(s.GetDSCard("combo"));s.DSChooseCard(s.GetDSCard("chamber"));s.PassAllResponses();return s;
    }
    @Test public void ScumDiscountRequiresOnlyAlienAndIndependentPilotAbility() {
        var s=scenario();var alien=s.GetDSCard("alien");assertEquals(alien.getBlueprint().getDeployCost()-1,s.GetDeployCost(alien),s.epsilon);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("trooper"));assertEquals(alien.getBlueprint().getDeployCost(),s.GetDeployCost(alien),s.epsilon);
    }
    @Test public void ScumAlterImmunityRequiresThreeControlledPalaceSites() {
        var s=scenario();var q=s.game().getModifiersQuerying();var combo=s.GetDSCard("combo");assertFalse(q.isImmuneToCardTitle(s.gameState(),combo,Title.Alter));
        s.MoveCardsToLocation(s.GetDSCard("dungeon"),s.GetDSCard("alien"));s.MoveCardsToLocation(s.GetDSCard("cavern"),s.GetDSCard("trooper"));assertTrue(q.isImmuneToCardTitle(s.gameState(),combo,Title.Alter));
        s.MoveCardsToLocation(s.GetDSCard("cavern"),s.GetLSCard("luke"));assertFalse(q.isImmuneToCardTitle(s.gameState(),combo,Title.Alter));
    }
    private void battle(boolean cancel) {
        var s=scenario();s.MoveCardsToLocation(s.GetDSCard("chamber"),s.GetLSCard("luke"));s.MoveCardsToTopOfDSLostPile(s.GetDSFiller(1),s.GetDSFiller(2));s.SkipToPhase(Phase.BATTLE);s.DSInitiateBattle(s.GetDSCard("chamber"));
        for(int i=0;i<12&&(s.DSGetDecision()==null||!s.DSCardActionAvailable(s.GetDSCard("combo"),"Examine"));i++) {if(s.LSGetDecision()!=null && s.LSDecisionAvailable("Optional"))s.LSPass();else if(s.DSGetDecision()!=null && s.DSDecisionAvailable("Optional"))s.DSPass();else break;}
        assertEquals(0,s.GetDSLostPileCount());s.DSUseCardAction(s.GetDSCard("combo"),"Examine");s.PassAllResponses();s.DSDismissRevealedCards();s.PassAllResponses();
        if(cancel) {s.DSChooseYes();s.PassAllResponses();s.DSPayRemainingForceLossFromReserveDeck();s.PassAllResponses();assertNull(s.gameState().getBattleState());assertEquals(1,s.GetDSLostPileCount());}
        else {s.DSChooseNo();s.PassAllResponses();assertNotNull(s.gameState().getBattleState());}
    }
    @Test public void InformationExchangeCanPayOneForceToCancelAfterScumRetrievesTwo() {battle(true);}
    @Test public void InformationExchangeMayKeepBattleAfterPeekingFourCards() {battle(false);}
}
