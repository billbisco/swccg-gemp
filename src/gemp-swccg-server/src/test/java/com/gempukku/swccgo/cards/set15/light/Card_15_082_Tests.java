package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_082_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_82");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("leia", "1_17");
        return new VirtualTableScenario(cards, new HashMap<>());
    }
    @Test
    public void GiftOfTheMentorAndSkywalkersStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Gift Of The Mentor & Skywalkers", card.getTitle());
        assertEquals(List.of("Gift Of The Mentor", "Skywalkers"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(5f, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertFalse(card.isHorizontal());
        assertEquals("", card.getLore());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        scn.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
    }
    private void battle(boolean obiWan, boolean leia, int expectedBonus) {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSFiller(1));
        if (obiWan) scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"));
        if (leia) scn.MoveCardsToLocation(site, scn.GetLSCard("leia"));
        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(scn.LSCardPlayAvailable(combo));
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        int before = scn.GetLSBattleDestinyCount();
        if (expectedBonus == 0) {
            assertFalse(scn.LSCardPlayAvailable(combo));
            return;
        }
        scn.LSPlayCard(combo);
        scn.PassAllResponses();
        assertEquals(before + expectedBonus, scn.GetLSBattleDestinyCount());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void GiftOfTheMentorAndSkywalkersAddsTwoWithObiWan() { battle(true, false, 2); }
    @Test public void GiftOfTheMentorAndSkywalkersAddsTwoWithLeia() { battle(false, true, 2); }
    @Test public void GiftOfTheMentorAndSkywalkersAddsFourWithBoth() { battle(true, true, 4); }
    @Test public void GiftOfTheMentorAndSkywalkersCannotPlayWithoutCompanion() { battle(false, false, 0); }
}
