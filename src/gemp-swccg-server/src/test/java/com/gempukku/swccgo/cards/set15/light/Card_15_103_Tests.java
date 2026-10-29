package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_103_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_103");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("chamber", "6_81");
        var dark = new HashMap<String,String>();
        dark.put("barrier", "1_249");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void MindfulOfTheFutureAndInconsequentialBarriersStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Mindful Of The Future & Inconsequential Barriers", card.getTitle());
        assertEquals(List.of("Mindful Of The Future", "Inconsequential Barriers"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(4f, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertFalse(card.isHorizontal());
        assertEquals("", card.getLore());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        scn.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD, Icon.EPISODE_I));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
    }
    @Test public void MindfulOfTheFutureAndInconsequentialBarriersDeploysUniqueBattlegroundForTwoForce() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var chamber = scn.GetLSCard("chamber");
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToBottomOfLSReserveDeck(chamber);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Deploy unique");
        scn.PassAllResponses();
        scn.LSChooseCard(chamber);
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("On which side"));
        scn.LSChoose("Left");
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, chamber.getZone());
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    private void peek(String choice) {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.CONTROL);
        var top = scn.GetTopOfLSForcePile();
        scn.LSPlayCard(combo, "Peek");
        scn.PassAllResponses();
        scn.LSDecided("");
        scn.PassAllResponses();
        scn.LSChoose(choice);
        scn.PassAllResponses();
        if (choice.equals("Return it")) assertEquals(top, scn.GetTopOfLSForcePile());
        else if (choice.equals("Place it under Force Pile")) assertEquals(top, scn.GetLSForcePile().getLast());
        else assertEquals(top, scn.GetLSReserveDeck().getLast());
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void MindfulOfTheFutureAndInconsequentialBarriersReturnsPeekedCard() { peek("Return it"); }
    @Test public void MindfulOfTheFutureAndInconsequentialBarriersPlacesPeekedCardUnderForce() { peek("Place it under Force Pile"); }
    @Test public void MindfulOfTheFutureAndInconsequentialBarriersPlacesPeekedCardUnderReserve() { peek("Place it under Reserve Deck"); }
    @Test public void MindfulOfTheFutureAndInconsequentialBarriersCancelsImperialBarrier() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var barrier = scn.GetDSCard("barrier");
        var trooper = scn.GetLSFiller(1);
        scn.StartGame();
        scn.MoveCardsToHand(combo, barrier, trooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(trooper);
        scn.LSChooseCard(scn.GetDSStartingLocation());
        for (int i = 0; i < 24; i++) {
            if (scn.DSGetDecision() != null && scn.DSCardPlayAvailable(barrier)) break;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        scn.DSPlayCard(barrier);
        for (int i = 0; i < 24; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(combo, "Cancel")) break;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        scn.LSPlayCard(combo, "Cancel");
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(barrier));
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
}
