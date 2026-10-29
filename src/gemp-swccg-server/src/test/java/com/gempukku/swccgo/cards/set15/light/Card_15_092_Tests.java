package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_092_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_92","scout","3_16","luke","1_19","leader","1_17","warroom","1_139","desert","6_85")),new HashMap<>(Map.of("vader","1_168","tarkin","1_179","bike","8_169")));
    }
    @Test public void InsertionPlanningAndCriticalErrorRevealedStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();
        assertEquals("Insertion Planning & Critical Error Revealed",card.getTitle());assertEquals(List.of("Insertion Planning","Critical Error Revealed"),card.getTitles());
        assertEquals(4f,card.getDestiny(),scn.epsilon);assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(CardSubtype.USED,card.getCardSubtype());assertEquals(0,card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());assertEquals("",card.getLore());assertFalse(card.isHorizontal());
        scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    private void peek(boolean bottom) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var room=scn.GetLSCard("warroom");
        scn.StartGame();scn.MoveLocationToTable(room);scn.MoveCardsToLocation(room,scn.GetLSCard("leader"));scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.CONTROL);
        var top=scn.GetDSReserveDeck().getFirst();scn.LSPlayCard(combo,"Peek");scn.PassAllResponses();scn.LSDecided("");scn.PassAllResponses();
        if(bottom)scn.LSChooseYes();else scn.LSChooseNo();scn.PassAllResponses();
        assertEquals(top,bottom?scn.GetDSReserveDeck().getLast():scn.GetDSReserveDeck().getFirst());assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void InsertionPlanningAndCriticalErrorRevealedCanKeepPeekedReserveCardOnTop() {peek(false);}
    @Test public void InsertionPlanningAndCriticalErrorRevealedCanPutPeekedReserveCardOnBottom() {peek(true);}
    @Test public void InsertionPlanningAndCriticalErrorRevealedRequiresLeaderAtOwnWarRoom() {
        var scn=scenario();scn.StartGame();scn.MoveLocationToTable(scn.GetLSCard("warroom"));scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard("leader"));scn.MoveCardsToHand(scn.GetLSCard("combo"));scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo"),"Peek"));
    }
    @Test public void InsertionPlanningAndCriticalErrorRevealedSubtractsThreeFromEnemyBattleDestiny() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveCardsToHand(combo);scn.MoveCardsToLocation(site,scn.GetLSCard("scout"),scn.GetLSCard("luke"),scn.GetDSCard("vader"));scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(5);scn.PrepareLSDestiny(0);scn.LSInitiateBattle(site);scn.PassAllResponses();scn.LSPlayCard(combo,"Subtract 3");scn.PassAllResponses();scn.SkipToEndOfPowerSegment(true);
        assertEquals(3,scn.GetDSTotalDestiny());assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    private void movingVehicle(int destiny,boolean removed) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var bike=scn.GetDSCard("bike");var driver=scn.GetDSCard("tarkin");var desert=scn.GetLSCard("desert");
        scn.StartGame();scn.MoveLocationToTable(desert);scn.MoveCardsToLocation(desert,scn.GetLSCard("scout"));scn.MoveCardsToLocation(scn.GetDSStartingLocation(),bike);scn.BoardAsPilot(bike,driver);scn.MoveCardsToHand(combo);scn.SkipToDSTurn(Phase.MOVE);scn.PrepareLSDestiny(destiny);
        scn.DSMoveCard(bike,desert);
        for(int i=0;i<24;i++) {
            if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Target character aboard"))break;
            if(scn.LSGetDecision()!=null)scn.LSPass();else if(scn.DSGetDecision()!=null)scn.DSPass();
        }
        scn.LSPlayCard(combo,"Target character aboard");scn.LSChooseCard(driver);scn.PassAllResponses();
        assertEquals(removed,scn.GetDSUsedPile().contains(driver));assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void InsertionPlanningAndCriticalErrorRevealedRemovesMovingPassengerAboveAbility() {movingVehicle(2,true);}
    @Test public void InsertionPlanningAndCriticalErrorRevealedDoesNotRemoveAtEqualAbility() {movingVehicle(1,false);}
}
