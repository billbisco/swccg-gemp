package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldRetrievalComboTests {
    private VirtualTableScenario scenario(String id, String first, String second) {
        var cards = new HashMap<String, String>();
        cards.put("combo", id);
        cards.put("first", first);
        cards.put("second", second);
        cards.put("other", "1_28");
        return new VirtualTableScenario(cards, new HashMap<>());
    }
    private void stats(String id, String title, String firstTitle, String secondTitle) {
        var scn = scenario(id, "1_11", "1_17");
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals(title, card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getCredits());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(3f, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertFalse(card.isHorizontal());
        assertEquals("", card.getLore());
        assertEquals(List.of(firstTitle, secondTitle), card.getTitles());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        scn.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
    }
    private void assertSelectable(VirtualTableScenario scn, PhysicalCardImpl card, boolean expected) {
        // Pile dialogs use tempN IDs and display unselectable cards. These fixtures have distinct blueprints.
        String[] ids = scn.LSGetADParam("blueprintId");
        String[] selectable = scn.LSGetADParam("selectable");
        assertNotNull(ids);
        assertNotNull(selectable);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(card.getBlueprintId(true))) {
                assertEquals(card.getBlueprint().getTitle(), expected, Boolean.parseBoolean(selectable[i]));
                return;
            }
        }
        fail("Expected card to be displayed: " + card.getBlueprint().getTitle());
    }
    private void retrieve(String id, String firstId, String secondId) {
        var scn = scenario(id, firstId, secondId);
        var combo = scn.GetLSCard("combo");
        var first = scn.GetLSCard("first");
        var second = scn.GetLSCard("second");
        var other = scn.GetLSCard("other");
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToTopOfLSLostPile(first, second, other);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.CONTROL);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Retrieve");
        scn.PassAllResponses();
        assertSelectable(scn, first, true);
        assertSelectable(scn, second, true);
        assertSelectable(scn, other, false);
        scn.LSChooseCard(second);
        scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(second));
        assertTrue(scn.GetLSLostPile().contains(first));
        assertTrue(scn.GetLSLostPile().contains(other));
        assertTrue(scn.GetLSUsedPile().contains(combo));
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }
    private void upload(String id, String firstId, String secondId) {
        var scn = scenario(id, firstId, secondId);
        var combo = scn.GetLSCard("combo");
        var first = scn.GetLSCard("first");
        var second = scn.GetLSCard("second");
        var other = scn.GetLSCard("other");
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.CONTROL);
        // Activation and Force setup must finish before placing the search targets.
        scn.MoveCardsToTopOfLSReserveDeck(first, second, other);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Take");
        scn.PassAllResponses();
        assertSelectable(scn, first, true);
        assertSelectable(scn, second, true);
        assertSelectable(scn, other, false);
        scn.LSChooseCard(first);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, first.getZone());
        assertTrue(scn.GetLSReserveDeck().contains(second));
        assertTrue(scn.GetLSUsedPile().contains(combo));
        assertEquals(force - 3, scn.GetLSForcePileCount());
    }
    @Test public void HansBackAndLeiasBackStatsAndKeywordsAreCorrect() { stats("15_84", "Han's Back & Leia's Back", "Han's Back", "Leia's Back"); }
    @Test public void HansBackAndLeiasBackRetrievesEitherPersonaForOneForce() { retrieve("15_84", "1_11", "1_17"); }
    @Test public void HansBackAndLeiasBackTakesEitherPersonaFromReserveForThreeForce() { upload("15_84", "1_11", "1_17"); }
    @Test public void ReturnOfAJediAndLukesBackStatsAndKeywordsAreCorrect() { stats("15_126", "Return Of A Jedi & Luke's Back", "Return Of A Jedi", "Luke's Back"); }
    @Test public void ReturnOfAJediAndLukesBackRetrievesEitherPersonaForOneForce() { retrieve("15_126", "1_21", "1_19"); }
    @Test public void ReturnOfAJediAndLukesBackTakesEitherPersonaFromReserveForThreeForce() { upload("15_126", "1_21", "1_19"); }
}
