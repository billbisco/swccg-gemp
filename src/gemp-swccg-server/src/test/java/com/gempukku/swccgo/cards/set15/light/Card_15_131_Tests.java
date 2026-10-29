package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_131_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_131");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("leia", "1_17");
        return new VirtualTableScenario(cards, new HashMap<>());
    }
    @Test
    public void SmokeScreenAndItsATrapStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Smoke Screen & It's A Trap!", card.getTitle());
        assertEquals(List.of("Smoke Screen", "It's A Trap!"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(2f, card.getDestiny(), scn.epsilon);
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
    private void reachPlay(VirtualTableScenario scn, String mode) {
        var combo = scn.GetLSCard("combo");
        for (int i = 0; i < 24; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(combo, mode)) return;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        fail("Expected " + mode + " response window");
    }
    @Test public void SmokeScreenAndItsATrapCancelsJustInitiatedSiteBattleForThreeForce() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSFiller(1));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        reachPlay(scn, "Cancel battle");
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Cancel battle");
        scn.PassAllResponses();
        assertNull(scn.gameState().getBattleState());
        assertEquals(force - 3, scn.GetLSForcePileCount());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void SmokeScreenAndItsATrapSubstitutesCharacterAbilityWithoutDrawingCard() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, luke, scn.GetDSFiller(1));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(1);
        var top = scn.GetTopOfLSReserveDeck();
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToPowerSegment();
        scn.LSChooseYes();
        reachPlay(scn, "Substitute");
        scn.LSPlayCard(combo, "Substitute");
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(scn.GetAbility(luke), scn.GetLSTotalDestiny());
        assertEquals(top, scn.GetTopOfLSReserveDeck());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
