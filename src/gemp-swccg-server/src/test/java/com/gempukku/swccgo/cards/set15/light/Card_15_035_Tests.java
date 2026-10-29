package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_035_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("effect", "15_35");
        cards.put("scout", "3_16");
        cards.put("trooper", "1_28");
        return new VirtualTableScenario(cards, new HashMap<>());
    }
    @Test
    public void IHopeShesAllRightStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("effect").getBlueprint();
        assertEquals("I Hope She's All Right", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        assertEquals(3f, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertTrue(card.isHorizontal());
        assertEquals("", card.getLore());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        scn.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
    }
    @Test
    public void IHopeShesAllRightDoesNotGrantOriginalScoutForfeitBonus() {
        var scn = scenario();
        var effect = scn.GetLSCard("effect");
        var scout = scn.GetLSCard("scout");
        scn.StartGame();
        scn.MoveCardsToHand(effect);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scout);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("trooper"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        int forfeit = scn.GetForfeit(scout);
        scn.LSPlayCard(effect);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, effect.getZone());
        assertEquals(forfeit, scn.GetForfeit(scout));
    }
    @Test
    public void IHopeShesAllRightCancelsWithoutTwoOccupiedBattlegrounds() {
        var scn = scenario();
        var effect = scn.GetLSCard("effect");
        scn.StartGame();
        scn.MoveCardsToHand(effect);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("scout"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(effect);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(effect));
    }
}
