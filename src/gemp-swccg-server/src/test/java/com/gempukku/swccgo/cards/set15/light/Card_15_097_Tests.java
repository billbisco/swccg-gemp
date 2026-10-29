package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_097_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_97");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("falcon", "1_143");
        cards.put("system", "1_127");
        cards.put("mods", "1_65");
        var dark = new HashMap<String,String>();
        dark.put("vader", "1_168");
        dark.put("wanted", "4_124");
        dark.put("warning", "8_122");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void LightManeuversAndJediLevitationStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Light Maneuvers & Jedi Levitation", card.getTitle());
        assertEquals(List.of("Light Maneuvers", "Jedi Levitation"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
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
    @Test public void LightManeuversAndJediLevitationUsesHighestCharacterAbilityForLostSearchCost() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var target = scn.GetLSFiller(1);
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetLSCard("obiwan"));
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToTopOfLSUsedPile(target);
        scn.SkipToPhase(Phase.CONTROL);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Take card");
        scn.PassAllResponses();
        scn.LSChooseCard(target);
        scn.PassAllResponses();
        assertTrue(scn.GetLSHand().contains(target));
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void LightManeuversAndJediLevitationDoublesSpecialModificationsUntilTurnEndAsUsed() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var falcon = scn.GetLSCard("falcon");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("system"));
        scn.MoveCardsToLocation(scn.GetLSCard("system"), falcon);
        scn.BoardAsPilot(falcon, scn.GetLSCard("luke"));
        scn.AttachCardsTo(falcon, scn.GetLSCard("mods"));
        scn.MoveCardsToHand(combo);
        scn.SkipToLSTurn(Phase.CONTROL);
        int maneuver = scn.GetManeuver(falcon);
        scn.LSPlayCard(combo, "Double Special");
        scn.PassAllResponses();
        assertEquals(maneuver + 2, scn.GetManeuver(falcon));
        assertTrue(scn.GetLSUsedPile().contains(combo));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(maneuver, scn.GetManeuver(falcon));
    }
    @Test public void LightManeuversAndJediLevitationCancelsIWantThatShipAsUsed() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var falcon = scn.GetLSCard("falcon");
        var effect = scn.GetDSCard("wanted");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("system"));
        scn.MoveCardsToLocation(scn.GetLSCard("system"), falcon);
        scn.BoardAsPilot(falcon, scn.GetLSCard("luke"));
        scn.AttachCardsTo(falcon, effect);
        scn.MoveCardsToHand(combo);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPlayCard(combo, "Cancel");
        scn.LSChooseCard(effect);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(effect));
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
}
