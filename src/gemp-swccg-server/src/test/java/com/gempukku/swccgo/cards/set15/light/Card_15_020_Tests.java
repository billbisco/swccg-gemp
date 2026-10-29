package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_020_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String,String>();
        cards.put("gift", "15_20");
        cards.put("droid", "1_5");
        cards.put("chamber", "6_81");
        var dark = new HashMap<String,String>();
        dark.put("alien", "1_171");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void AGiftStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("gift").getBlueprint();
        assertEquals("A Gift", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        assertEquals(4f, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertTrue(card.isHorizontal());
        assertEquals("", card.getLore());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        scn.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
        assertTrue(card.isImmuneToCardTitle(Title.Control));
    }
    @Test
    public void AGiftDeploysOnlyAfterDroidMovesToAudienceChamberAndMakesItUndercover() {
        var scn = scenario();
        var gift = scn.GetLSCard("gift");
        var droid = scn.GetLSCard("droid");
        var chamber = scn.GetLSCard("chamber");
        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.MoveCardsToLocation(chamber, scn.GetDSCard("alien"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), droid);
        scn.MoveCardsToHand(gift);
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.LSCardPlayAvailable(gift));
        assertTrue(scn.IsAdjacentTo(chamber, scn.GetDSStartingLocation()));
        scn.LSMoveCard(droid, chamber);
        // Stop at the moved-card response window instead of passing the immediate Effect opportunity.
        for (int i = 0; i < 24; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(gift)) break;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        float drainBefore = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), chamber, scn.DS);
        scn.LSPlayCard(gift);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        assertEquals(droid, gift.getAttachedTo());
        assertTrue(droid.isUndercover());
        assertEquals(drainBefore - 1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), chamber, scn.DS), scn.epsilon);
    }
    @Test public void AGiftIsLostWhenAttachedDroidIsNoLongerOnTatooine() {
        var scn = scenario();
        var gift = scn.GetLSCard("gift");
        var droid = scn.GetLSCard("droid");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), droid);
        scn.AttachCardsTo(droid, gift);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSLostPile().contains(gift));
        assertNull(gift.getAttachedTo());
    }
}
