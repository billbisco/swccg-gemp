package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_085_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_85","obi","1_21","leia","1_17","desert","1_133")),new HashMap<>(Map.of("spy","1_163","vader","1_168")));
    }
    @Test public void HelpMeObiWanKenobiAndDoubleAgentStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();assertEquals("Help Me Obi-Wan Kenobi & Double Agent",card.getTitle());assertEquals(List.of(Title.Help_Me_Obi_Wan_Kenobi,Title.Double_Agent),card.getTitles());assertEquals(Uniqueness.UNRESTRICTED,card.getUniqueness());
        assertEquals(5f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.USED,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    private void spy(int destiny,boolean undercover,boolean lost) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var enemy=scn.GetDSCard("spy");var leia=scn.GetLSCard("leia");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveCardsToLocation(site,leia,enemy);if(undercover)scn.MakeCardGoUndercover(enemy);scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.CONTROL);scn.PrepareLSDestiny(destiny);
        scn.LSPlayCard(combo);scn.LSChooseCard(leia);scn.LSChooseCard(enemy);scn.PassAllResponses();assertEquals(lost,scn.GetDSLostPile().contains(enemy));assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void HelpMeObiWanKenobiAndDoubleAgentLosesSpyAboveTwo() {spy(3,false,true);}
    @Test public void HelpMeObiWanKenobiAndDoubleAgentDoesNotLoseSpyAtTwo() {spy(2,false,false);}
    @Test public void HelpMeObiWanKenobiAndDoubleAgentAddsTwoAgainstUndercoverSpy() {spy(1,true,true);}
    @Test public void HelpMeObiWanKenobiAndDoubleAgentAppliesRemoteAbilityAndConsumesItForTurn() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var obi=scn.GetLSCard("obi");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveCardsToLocation(scn.GetLSStartingLocation(),obi);scn.MoveCardsToLocation(site,scn.GetLSFiller(1),scn.GetDSCard("vader"));scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.BATTLE);scn.LSInitiateBattle(site);scn.PassAllResponses();
        assertEquals(0,scn.GetLSBattleDestinyCount());float before=scn.game().getModifiersQuerying().getAbilityForBattleDestiny(scn.gameState(),obi);
        scn.LSPlayCard(combo,"Apply Obi-Wan");scn.LSDecided(3);scn.PassAllResponses();assertEquals(1,scn.GetLSBattleDestinyCount());assertEquals(before-3,scn.game().getModifiersQuerying().getAbilityForBattleDestiny(scn.gameState(),obi),scn.epsilon);assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void HelpMeObiWanKenobiAndDoubleAgentMovesAdjacentRebelAsReact() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var leia=scn.GetLSCard("leia");var desert=scn.GetLSCard("desert");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveLocationToTable(desert);scn.MoveCardsToLocation(desert,leia);scn.MoveCardsToLocation(site,scn.GetLSFiller(1),scn.GetDSCard("vader"));scn.MoveCardsToHand(combo);scn.SkipToDSTurn(Phase.BATTLE);scn.DSInitiateBattle(site);
        for(int i=0;i<24;i++) {if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Move Rebel"))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}
        int force=scn.GetLSForcePileCount();scn.LSPlayCard(combo,"Move Rebel");scn.LSChooseCard(leia);scn.PassAllResponses();assertTrue(scn.CardsAtLocation(site,leia));assertEquals(force-1,scn.GetLSForcePileCount());assertTrue(scn.GetLSUsedPile().contains(combo));
    }
}
