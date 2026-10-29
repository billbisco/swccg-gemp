package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_096_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo", "15_96", "eyes", "5_56", "shoo", "4_59", "droid", "1_5", "chamber", "6_85")), new HashMap<>(Map.of("rat", "7_215")));
    }
    @Test public void KeepYourEyesOpenAndShooStatsAndKeywordsAreCorrect() {
        var scn = scenario(); var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Keep Your Eyes Open & Shoo! Shoo!", card.getTitle());
        assertEquals(List.of(new TitleComponent(Title.Keep_Your_Eyes_Open, Uniqueness.UNRESTRICTED), new TitleComponent("Shoo! Shoo!", Uniqueness.UNIQUE)), card.getTitleComponents());
        assertEquals(5f, card.getDestiny(), scn.epsilon); assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(0, card.getCredits()); assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity()); assertEquals("", card.getLore()); assertFalse(card.isHorizontal());
        scn.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of()); scn.BlueprintPersonaCheck(card, List.of());
    }
    private void look(boolean dark) {
        var scn = scenario(); var combo = scn.GetLSCard("combo"); scn.StartGame();
        scn.MoveCardsToHand(combo);
        if (dark) scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1), scn.GetDSFiller(2));
        else scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1), scn.GetLSFiller(2));
        scn.SkipToLSTurn(Phase.CONTROL);
        var before = new ArrayList<>(dark ? scn.GetDSLostPile() : scn.GetLSLostPile());
        scn.LSPlayCard(combo, "Look at Lost Pile"); scn.PassAllResponses(); scn.LSDecided(""); scn.PassAllResponses();
        assertEquals(before, dark ? scn.GetDSLostPile() : scn.GetLSLostPile()); assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void KeepYourEyesOpenAndShooLooksAtOwnLostPileWithoutChangingOrder() { look(false); }
    @Test public void KeepYourEyesOpenAndShooLooksAtOpponentsLostPileWithoutChangingOrder() { look(true); }
    @Test public void KeepYourEyesOpenAndShooRelocatesCreatureWithDroidOnTable() {
        var scn = scenario(); var combo = scn.GetLSCard("combo"); var rat = scn.GetDSCard("rat");
        scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE); scn.MoveLocationToTable(scn.GetLSCard("chamber"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), rat); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("droid"));
        scn.MoveCardsToHand(combo); scn.SkipToPhase(Phase.CONTROL);
        scn.LSPlayCard(combo, "Move an opponent"); scn.LSChooseCard(rat); scn.LSChooseCard(scn.GetLSCard("chamber")); scn.PassAllResponses();
        assertEquals(scn.GetLSCard("chamber"), rat.getAtLocation()); assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void KeepYourEyesOpenAndShooDoesNotRelocateWithoutDroid() {
        var scn = scenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("chamber")); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("rat"));
        scn.MoveCardsToHand(scn.GetLSCard("combo")); scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo"), "Move an opponent"));
    }
    @Test public void KeepYourEyesOpenAndShooCancelsCreatureAttackWithDroidOnTable() {
        var scn = scenario(); var combo = scn.GetLSCard("combo"); var rat = scn.GetDSCard("rat");
        scn.StartGame(); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), rat, scn.GetLSFiller(1));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("droid")); scn.MoveCardsToHand(combo);
        scn.SkipToPhase(Phase.BATTLE); scn.DSUseCardAction(rat, "Initiate attack");
        for (int i=0; i<24; i++) {
            if (scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo, "Cancel attack")) break;
            if (scn.LSGetDecision()!=null) scn.LSPass(); else if(scn.DSGetDecision()!=null) scn.DSPass();
        }
        scn.LSPlayCard(combo, "Cancel attack"); scn.PassAllResponses();
        assertNull(scn.gameState().getAttackState()); assertTrue(scn.GetLSUsedPile().contains(combo));
        assertEquals(scn.GetDSStartingLocation(), scn.GetLSFiller(1).getAtLocation());
    }
}
