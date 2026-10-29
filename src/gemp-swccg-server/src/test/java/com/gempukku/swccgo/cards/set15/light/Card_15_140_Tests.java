package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_140_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_140");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("leia", "1_17");
        var dark = new HashMap<String,String>();
        dark.put("vader", "1_168");
        dark.put("presence", "1_227");
        dark.put("warning", "8_122");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void ThisIsMoreLikeItAndIHaveAReallyBadFeelingAboutThisStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("This Is More Like It & I Have A Really Bad Feeling About This", card.getTitle());
        assertEquals(List.of("This Is More Like It", "I Have A Really Bad Feeling About This"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        assertEquals(3f, card.getDestiny(), scn.epsilon);
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
    @Test public void ThisIsMoreLikeItAndIHaveAReallyBadFeelingAboutThisRelocatesEffectForThreeForceAsLost() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var effect = scn.GetDSCard("presence");
        var destination = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.AttachCardsTo(scn.GetLSStartingLocation(), effect);
        scn.MoveCardsToHand(combo);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Relocate");
        scn.LSChooseCard(effect);
        scn.LSChooseCard(destination);
        scn.PassAllResponses();
        assertEquals(destination, effect.getAttachedTo());
        assertEquals(force - 3, scn.GetLSForcePileCount());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void ThisIsMoreLikeItAndIHaveAReallyBadFeelingAboutThisCannotRelocateAlterImmuneEffect() {
        var scn = scenario();
        scn.StartGame();
        scn.AttachCardsTo(scn.GetLSStartingLocation(), scn.GetDSCard("warning"));
        scn.MoveCardsToHand(scn.GetLSCard("combo"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo"), "Relocate"));
    }
    @Test public void ThisIsMoreLikeItAndIHaveAReallyBadFeelingAboutThisLosesTwoForceToReduceAttritionAsUsed() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(5);
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(6, scn.GetUnpaidLSAttrition());
        if (scn.LSGetDecision() == null) scn.DSPass();
        int lost = scn.GetLSLostPileCount();
        scn.LSPlayCard(combo, "Lose Force");
        scn.LSDecided("2");
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        assertEquals(4, scn.GetUnpaidLSAttrition());
        assertEquals(lost + 2, scn.GetLSLostPileCount());
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
}
