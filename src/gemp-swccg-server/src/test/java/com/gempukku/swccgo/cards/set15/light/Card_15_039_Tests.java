package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_039_Tests {
    private VirtualTableScenario scenario() {return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_39","senator","12_32","luke","1_19","obi","1_21")),new HashMap<>(Map.of("trooper","1_194","blaster","1_317","vader","1_168")));}
    private void chooseTrigger(VirtualTableScenario scn,PhysicalCardImpl combo,String label) {
        for(int i=0;i<24;i++) {if(scn.LSGetDecision()!=null && scn.LSCardActionAvailable(combo,label))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}
        scn.LSUseCardAction(combo,label);
    }
    @Test public void IWillNotDeferAndPleaToTheCourtStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();assertEquals("I Will Not Defer & Plea To The Court",card.getTitle());assertEquals(List.of("I Will Not Defer",Title.Plea_To_The_Court),card.getTitles());assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(2f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.POLITICAL,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.EFFECT));scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.EPISODE_I,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void IWillNotDeferAndPleaToTheCourtStacksSenatorForNextBattlegroundDrainOnly() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var senator=scn.GetLSCard("senator");var first=scn.GetDSStartingLocation();var second=scn.GetLSStartingLocation();scn.StartGame();scn.MoveCardsToSideOfTable(combo);scn.MoveCardsToHand(senator);scn.MoveCardsToLocation(first,scn.GetLSCard("luke"));scn.MoveCardsToLocation(second,scn.GetLSCard("obi"));scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(combo,"Add 2");scn.PassAllResponses();assertEquals(Zone.STACKED,senator.getZone());scn.DSPass();assertFalse(scn.LSCardActionAvailable(combo,"Add 2"));scn.LSForceDrainAt(first);scn.PassAllResponses();assertEquals(3,scn.GetForceDrainTotal());for(int i=0;i<3;i++){scn.DSChooseCard(scn.GetTopOfDSReserveDeck());scn.PassAllResponses();}scn.DSPass();scn.LSForceDrainAt(second);scn.PassAllResponses();assertEquals(1,scn.GetForceDrainTotal());
    }
    @Test public void IWillNotDeferAndPleaToTheCourtStacksSenatorToCancelDrain() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var senator=scn.GetLSCard("senator");var site=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToSideOfTable(combo);scn.MoveCardsToHand(senator);scn.MoveCardsToLocation(site,scn.GetDSCard("vader"));scn.SkipToDSTurn(Phase.CONTROL);int lost=scn.GetLSLostPileCount();scn.DSForceDrainAt(site);chooseTrigger(scn,combo,"Cancel Force drain");scn.PassAllResponses();assertEquals(Zone.STACKED,senator.getZone());assertEquals(lost,scn.GetLSLostPileCount());assertFalse(scn.IsActiveForceDrain());
    }
    @Test public void IWillNotDeferAndPleaToTheCourtPeaceActivatesTwoAfterOpponentInitiatesBattle() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var site=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToSideOfTable(combo);scn.StackCardsOn(combo,scn.GetLSCard("senator"));scn.MoveCardsToLocation(site,scn.GetLSCard("luke"),scn.GetDSCard("vader"));scn.SkipToDSTurn(Phase.BATTLE);int force=scn.GetLSForcePileCount();scn.DSInitiateBattle(site);chooseTrigger(scn,combo,"Activate up to 2");scn.LSDecided(2);scn.PassAllResponses();assertEquals(force+2,scn.GetLSForcePileCount());
    }
    @Test public void IWillNotDeferAndPleaToTheCourtJusticeRedrawsWeaponDestiny() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var luke=scn.GetLSCard("luke");var site=scn.GetDSStartingLocation();var blaster=scn.GetDSCard("blaster");var trooper=scn.GetDSCard("trooper");scn.StartGame();scn.MoveCardsToSideOfTable(combo);scn.StackCardsOn(combo,scn.GetLSCard("senator"));scn.MoveCardsToLocation(site,luke,trooper);scn.AttachCardsTo(trooper,blaster);scn.SkipToDSTurn(Phase.BATTLE);scn.DSInitiateBattle(site);scn.PassAllResponses();scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(0),scn.GetDSDestiny(7));scn.DSUseCardAction(blaster);scn.DSChooseCard(luke);chooseTrigger(scn,combo,"Cancel destiny");scn.PassAllResponses();assertFalse(luke.isHit());
    }
}
