package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_032_Tests {
    private VirtualTableScenario scenario() {return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_32","trooper","1_28","jawa","1_12","luke","1_19","junkyard","12_87")),new HashMap<>(Map.of("vader","1_168","choke","1_264")));}
    private void trigger(VirtualTableScenario scn,PhysicalCardImpl combo) {for(int i=0;i<24;i++){if(scn.LSGetDecision()!=null && scn.LSCardActionAvailable(combo,"Cancel all"))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}scn.LSUseCardAction(combo,"Cancel all");}
    @Test public void CreditsWillDoFineAndWereLeavingStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();assertEquals("Credits Will Do Fine & We're Leaving",card.getTitle());assertEquals(List.of(Title.Credits_Will_Do_Fine,"We're Leaving"),card.getTitles());assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());scn.BlueprintCardTypeCheck(card,List.of(CardType.EFFECT));scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.EPISODE_I,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void CreditsWillDoFineAndWereLeavingCostsTwoAndStacksExtraLossAtJunkyard() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var site=scn.GetLSCard("junkyard");var loss=scn.GetDSFiller(1);scn.StartGame();scn.MoveLocationToTable(site);scn.MoveCardsToLocation(site,scn.GetLSCard("luke"));scn.MoveCardsToHand(combo,loss);scn.SkipToLSTurn(Phase.DEPLOY);int force=scn.GetLSForcePileCount();scn.LSPlayCard(combo);scn.PassAllResponses();assertEquals(force-2,scn.GetLSForcePileCount());scn.SkipToLSTurn(Phase.CONTROL);scn.LSForceDrainAt(site);scn.PassAllResponses();scn.DSChooseCard(loss);scn.PassAllResponses();assertEquals(combo,loss.getStackedOn());assertFalse(scn.GetDSLostPile().contains(loss));
    }
    @Test public void CreditsWillDoFineAndWereLeavingStacksExtraLossWhenWinningAtJunkyard() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var site=scn.GetLSCard("junkyard");var loss=scn.GetDSFiller(1);scn.StartGame();scn.MoveLocationToTable(site);scn.MoveCardsToSideOfTable(combo);scn.MoveCardsToLocation(site,scn.GetLSCard("luke"),scn.GetDSFiller(2));scn.MoveCardsToHand(loss);scn.SkipToLSTurn(Phase.BATTLE);scn.LSInitiateBattle(site);scn.SkipToEndOfPowerSegment(false);scn.PassAllResponses();scn.DSChooseCard(loss);scn.PassAllResponses();assertEquals(combo,loss.getStackedOn());assertFalse(scn.GetDSLostPile().contains(loss));
    }
    private void forfeit(boolean choke,boolean ownBattle) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var jawa=scn.GetLSCard("jawa");var trooper=scn.GetLSCard("trooper");var site=scn.GetDSStartingLocation();var stacked=scn.GetDSFiller(1);scn.StartGame();scn.MoveCardsToSideOfTable(combo);scn.StackCardsFaceDownOn(combo,stacked);scn.MoveCardsToLocation(site,jawa,trooper,scn.GetDSCard("vader"));scn.MoveCardsToHand(scn.GetDSCard("choke"));if(ownBattle)scn.SkipToLSTurn(Phase.BATTLE);else scn.SkipToDSTurn(Phase.BATTLE);scn.PrepareDSDestiny(7);if(ownBattle)scn.LSInitiateBattle(site);else scn.DSInitiateBattle(site);scn.PassAllResponses();
        if(choke){scn.DSPlayCard(scn.GetDSCard("choke"),"Choke a Rebel");scn.DSChooseCard(trooper);scn.PassAllResponses();assertTrue(scn.GetLSLostPile().contains(trooper));}
        scn.SkipToDamageSegment(true);scn.PassAllResponses();assertTrue(scn.GetUnpaidLSBattleDamage()>scn.GetForfeit(jawa)+(choke?0:scn.GetForfeit(trooper)));if(!choke){scn.LSChooseCard(trooper);scn.PassAllResponses();}scn.LSChooseCard(jawa);
        if(choke||ownBattle){for(int i=0;i<24;i++){if(scn.AwaitingLSBattleDamagePayment())break;if(scn.LSGetDecision()!=null){assertFalse(scn.LSCardActionAvailable(combo,"Cancel all"));scn.LSPass();}else scn.DSPass();}assertTrue(scn.GetUnpaidLSBattleDamage()>0);assertEquals(Zone.SIDE_OF_TABLE,combo.getZone());return;}
        int lostBefore=scn.GetLSLostPileCount();trigger(scn,combo);scn.PassAllResponses();assertFalse(scn.gameState().isDuringBattle());assertEquals(lostBefore,scn.GetLSLostPileCount());assertEquals(Zone.OUT_OF_PLAY,combo.getZone());assertTrue(scn.GetDSUsedPile().contains(stacked));assertFalse(scn.GetDSLostPile().contains(stacked));
    }
    @Test public void CreditsWillDoFineAndWereLeavingCancelsDamageOnlyAfterAllParticipantsForfeited() {forfeit(false,false);}
    @Test public void CreditsWillDoFineAndWereLeavingDoesNotCountChokedParticipantAsForfeited() {forfeit(true,false);}
    @Test public void CreditsWillDoFineAndWereLeavingDoesNotCancelOwnInitiatedBattleDamage() {forfeit(false,true);}
}
