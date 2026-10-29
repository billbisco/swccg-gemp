package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_050_Tests {
    private VirtualTableScenario scenario() {return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_50","effect","1_45","luke","1_19")),new HashMap<>(Map.of("boba","5_91","vader","1_168")));}
    @Test public void ScrambleAndHidingInTheGarbageStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();assertEquals("Scramble & Hiding In The Garbage",card.getTitle());assertEquals(List.of(Title.Scramble,"Hiding In The Garbage"),card.getTitles());assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(4f,card.getDestiny(),scn.epsilon);assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.EFFECT));scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void ScrambleAndHidingInTheGarbageDeploysFreeAndStopsPilotAbilityExceptVader() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var boba=scn.GetDSCard("boba");var vader=scn.GetDSCard("vader");scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),boba,vader);scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.DEPLOY);int force=scn.GetLSForcePileCount();
        assertEquals(3f,scn.game().getModifiersQuerying().getAbilityForBattleDestiny(scn.gameState(),boba),scn.epsilon);scn.LSPlayCard(combo);scn.PassAllResponses();assertEquals(force,scn.GetLSForcePileCount());assertEquals(0f,scn.game().getModifiersQuerying().getAbilityForBattleDestiny(scn.gameState(),boba),scn.epsilon);assertEquals(6f,scn.game().getModifiersQuerying().getAbilityForBattleDestiny(scn.gameState(),vader),scn.epsilon);assertEquals(Zone.SIDE_OF_TABLE,combo.getZone());
    }
    private void search(boolean matches) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var effect=scn.GetLSCard("effect");var luke=scn.GetLSCard("luke");scn.StartGame();scn.MoveCardsToSideOfTable(combo);scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("vader"));scn.SkipToLSTurn(Phase.CONTROL);scn.MoveCardsToTopOfLSUsedPile(scn.GetLSForcePile().toArray(new PhysicalCardImpl[0]));scn.MoveCardsToTopOfLSForcePile(luke,effect);int used=scn.GetLSUsedPileCount();
        scn.LSUseCardAction(combo,"Use Force");scn.LSChoose(matches?"Rebel":"Weapon");scn.PassAllResponses();assertEquals(0,scn.GetLSForcePileCount());assertTrue(scn.GetLSUsedPile().contains(effect));if(matches){assertEquals(Zone.HAND,luke.getZone());assertEquals(used+1,scn.GetLSUsedPileCount());}else{assertTrue(scn.GetLSUsedPile().contains(luke));assertEquals(used+2,scn.GetLSUsedPileCount());}
    }
    @Test public void ScrambleAndHidingInTheGarbageUsesForceUntilDeclaredTypeAndTakesItIntoHand() {search(true);}
    @Test public void ScrambleAndHidingInTheGarbageStopsWhenForcePileDepletedWithoutMatch() {search(false);}
    @Test public void ScrambleAndHidingInTheGarbageCancelsWhenOpponentHasNoPilotAtSite() {
        var scn=scenario();var combo=scn.GetLSCard("combo");scn.StartGame();scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.DEPLOY);scn.LSPlayCard(combo);scn.PassAllResponses();assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
