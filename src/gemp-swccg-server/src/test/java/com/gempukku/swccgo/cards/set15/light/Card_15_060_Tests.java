package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_060_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_60");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("xwing", "1_146");
        cards.put("system", "1_127");
        return new VirtualTableScenario(cards, new HashMap<>());
    }
    @Test
    public void AFewManeuversAndHansDiceStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("A Few Maneuvers & Han's Dice", card.getTitle());
        assertEquals(List.of("A Few Maneuvers", "Han's Dice"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
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
    @Test public void AFewManeuversAndHansDiceAddsHyperspeedAndManeuverUntilEndOfTurn() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("system"));
        scn.MoveCardsToLocation(scn.GetLSCard("system"), xwing);
        scn.MoveCardsToHand(combo);
        scn.SkipToLSTurn(Phase.CONTROL);
        int maneuver = scn.GetManeuver(xwing);
        int hyperspeed = scn.GetHyperspeed(xwing);
        scn.LSPlayCard(combo, "Add 2");
        scn.LSChooseCard(xwing);
        scn.PassAllResponses();
        assertEquals(maneuver + 2, scn.GetManeuver(xwing));
        assertEquals(hyperspeed + 2, scn.GetHyperspeed(xwing));
        assertTrue(scn.GetLSUsedPile().contains(combo));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(maneuver, scn.GetManeuver(xwing));
        assertEquals(hyperspeed, scn.GetHyperspeed(xwing));
    }
    @Test public void AFewManeuversAndHansDicePaysOneForceToRedrawBattleDestiny() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSFiller(1));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(6);
        scn.PrepareLSDestiny(1);
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToPowerSegment();
        scn.LSChooseYes();
        for (int i = 0; i < 24; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(combo, "re-draw")) break;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "re-draw");
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(6, scn.GetLSTotalDestiny());
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
}
