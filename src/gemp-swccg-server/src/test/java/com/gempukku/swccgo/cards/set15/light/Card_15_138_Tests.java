package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_138_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_138");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("leia", "1_17");
        var dark = new HashMap<String,String>();
        dark.put("vader", "1_168");
        dark.put("tarkin", "1_179");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void TheForceIsStrongWithThisOneAndJediPresenceStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("The Force Is Strong With This One & Jedi Presence", card.getTitle());
        assertEquals(List.of("The Force Is Strong With This One", "Jedi Presence"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
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
    private void destiny(String opponent, int expected) {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard(opponent));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        int before = scn.GetLSBattleDestinyCount();
        scn.LSPlayCard(combo, "Add battle destiny");
        scn.PassAllResponses();
        assertEquals(before + expected, scn.GetLSBattleDestinyCount());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void TheForceIsStrongWithThisOneAndJediPresenceAddsTwoAgainstVader() { destiny("vader", 2); }
    @Test public void TheForceIsStrongWithThisOneAndJediPresenceAddsOneAgainstOtherQualifiedImperial() { destiny("tarkin", 1); }
    @Test public void TheForceIsStrongWithThisOneAndJediPresenceDoublesOtherRebelsForOneForce() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetDSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var obiwan = scn.GetLSCard("obiwan");
        var leia = scn.GetLSCard("leia");
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, luke, obiwan, leia, scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.LSCardPlayAvailable(combo, "Add battle destiny"));
        assertTrue(scn.LSCardPlayAvailable(combo, "Double power"));
        int lukePower = scn.GetPower(luke);
        int leiaPower = scn.GetPower(leia);
        int jediPower = scn.GetPower(obiwan);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Double power");
        scn.LSChooseCard(obiwan);
        scn.PassAllResponses();
        assertEquals(lukePower * 2, scn.GetPower(luke));
        assertEquals(leiaPower * 2, scn.GetPower(leia));
        assertEquals(jediPower, scn.GetPower(obiwan));
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
