package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_151_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_151");
        cards.put("chewie", "15_3");
        cards.put("lando", "5_5");
        cards.put("han", "1_14");
        var dark = new HashMap<String,String>();
        dark.put("vader", "1_168");
        dark.put("choice", "5_119");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void WookieeStrangleAndWellFindHanStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Wookiee Strangle & We'll Find Han", card.getTitle());
        assertEquals(List.of("Wookiee Strangle", "We'll Find Han"), card.getTitles());
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
    private void strangle(int lightDestiny, int darkDestiny, boolean lost) {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var chewie = scn.GetLSCard("chewie");
        var victim = scn.GetDSFiller(1);
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), chewie, victim);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(lightDestiny);
        scn.PrepareDSDestiny(darkDestiny);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Target a non-droid");
        scn.LSChooseCard(victim);
        scn.LSChooseCard(chewie);
        scn.PassAllResponses();
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertEquals(lost, scn.GetDSLostPile().contains(victim));
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void WookieeStrangleAndWellFindHanWinsWithGreaterTotal() { strangle(6, 0, true); }
    @Test public void WookieeStrangleAndWellFindHanDoesNotWinWithEqualTotal() { strangle(0, 6, false); }
    @Test public void WookieeStrangleAndWellFindHanDoesNotDeployHan() {
        var scn = scenario();
        scn.StartGame();
        scn.MoveCardsToHand(scn.GetLSCard("combo"), scn.GetLSCard("han"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("chewie"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo")));
    }
    private void reduceAttrition(int destiny, int expected) {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("chewie"), scn.GetLSCard("lando"), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(destiny);
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToEndOfPowerSegment(true);
        // Premiere Vader adds 1 to his battle destiny.
        assertEquals(destiny + 1, scn.GetUnpaidLSAttrition());
        if (scn.LSGetDecision() == null) scn.DSPass();
        scn.LSPlayCard(combo, "Reduce attrition");
        scn.PassCardPlayResponses();
        assertEquals(expected, scn.GetUnpaidLSAttrition());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void WookieeStrangleAndWellFindHanReducesAttritionByThree() { reduceAttrition(5, 3); }
    @Test public void WookieeStrangleAndWellFindHanCannotReduceAttritionBelowZero() { reduceAttrition(1, 0); }
    @Test public void WookieeStrangleAndWellFindHanCancelsIHadNoChoice() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var choice = scn.GetDSCard("choice");
        var lando = scn.GetLSCard("lando");
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), lando);
        scn.AttachCardsTo(lando, choice);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPlayCard(combo, "Cancel");
        scn.LSChooseCard(choice);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(choice));
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
