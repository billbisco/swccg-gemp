package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_076_Tests {
    private VirtualTableScenario scenario() {return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_76","luke","1_19")),new HashMap<>(Map.of("vader","1_168")));}
    @Test public void EitherWayYouWinAndEventuallyYoullLoseStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();assertEquals("Either Way, You Win & Eventually You'll Lose",card.getTitle());assertEquals(List.of(new TitleComponent("Either Way, You Win",Uniqueness.UNIQUE),new TitleComponent("Eventually You'll Lose",Uniqueness.UNIQUE)),card.getTitleComponents());assertEquals(4f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.USED_OR_LOST,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.EPISODE_I,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void EitherWayYouWinAndEventuallyYoullLoseMovesUsedTopToReserveWhenBattleStarts() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var top=scn.GetLSFiller(1);var site=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToLocation(site,scn.GetLSCard("luke"),scn.GetDSCard("vader"));scn.MoveCardsToHand(combo);scn.SkipToDSTurn(Phase.BATTLE);scn.MoveCardsToTopOfLSUsedPile(top);scn.DSInitiateBattle(site);
        for(int i=0;i<24;i++) {if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Place top"))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}
        scn.LSPlayCard(combo,"Place top");scn.PassAllResponses();assertEquals(top,scn.GetTopOfLSReserveDeck());assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void EitherWayYouWinAndEventuallyYoullLosePaysThreeAndLosesAllTriplicateHandCards() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var one=scn.GetDSFiller(1);var two=scn.GetDSFiller(2);var three=scn.GetDSFiller(3);var keep=scn.GetDSCard("vader");scn.StartGame();scn.MoveCardsToHand(combo,one,two,three,keep);scn.SkipToLSTurn(Phase.CONTROL);int force=scn.GetLSForcePileCount();scn.LSPlayCard(combo,"Reveal opponent");scn.PassAllResponses();scn.LSDecided("");scn.PassAllResponses();scn.DSChooseCard(one);scn.PassAllResponses();scn.DSChooseCard(two);scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().containsAll(List.of(one,two,three)));assertEquals(Zone.HAND,keep.getZone());assertEquals(force-3,scn.GetLSForcePileCount());assertTrue(scn.GetLSLostPile().contains(combo));
    }
    private void activate(boolean opponentEmpty) {
        var scn=scenario();var combo=scn.GetLSCard("combo");scn.StartGame();scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.CONTROL);if(opponentEmpty)scn.MoveCardsToTopOfDSUsedPile(scn.GetDSReserveDeck().toArray(new PhysicalCardImpl[0]));int ls=scn.GetLSForcePileCount();int ds=scn.GetDSForcePileCount();
        scn.LSPlayCard(combo,"Make opponent");scn.PassAllResponses();scn.DSChoose("Activate up to 2 Force");if(!opponentEmpty){scn.DSDecided(2);scn.PassAllResponses();}scn.LSDecided(4);scn.PassAllResponses();assertEquals(ls+4,scn.GetLSForcePileCount());assertEquals(ds+(opponentEmpty?0:2),scn.GetDSForcePileCount());assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void EitherWayYouWinAndEventuallyYoullLoseLetsOpponentChooseTwoAndPlayerFourActivation() {activate(false);}
    @Test public void EitherWayYouWinAndEventuallyYoullLoseStillActivatesPlayerWhenOpponentReserveEmpty() {activate(true);}
    @Test public void EitherWayYouWinAndEventuallyYoullLoseLetsOpponentChooseRetrieval() {
        var scn=scenario();var combo=scn.GetLSCard("combo");scn.StartGame();scn.MoveCardsToHand(combo);scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1),scn.GetLSFiller(2),scn.GetLSFiller(3));scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1));scn.SkipToLSTurn(Phase.CONTROL);int ls=scn.GetLSUsedPileCount();int ds=scn.GetDSUsedPileCount();
        scn.LSPlayCard(combo,"Make opponent");scn.PassAllResponses();scn.DSChoose("Retrieve 1 Force");scn.PassAllResponses();assertEquals(ls+3,scn.GetLSUsedPileCount());assertEquals(ds+1,scn.GetDSUsedPileCount());assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
