package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_129_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_129");
        cards.put("luke", "1_19");
        cards.put("obiwan", "1_21");
        cards.put("portal", "6_66");
        cards.put("blast", "2_45");
        cards.put("droid", "1_5");
        var dark = new HashMap<String,String>();
        dark.put("boba", "5_91");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void SkullAndMandalorianMishapStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Skull & Mandalorian Mishap", card.getTitle());
        assertEquals(List.of("Skull", "Mandalorian Mishap"), card.getTitles());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(4f, card.getDestiny(), scn.epsilon);
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
    private void assertSelectable(VirtualTableScenario scn, String blueprint, boolean expected) {
        String[] ids = scn.LSGetADParam("blueprintId");
        String[] selectable = scn.LSGetADParam("selectable");
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(blueprint)) {
                assertEquals(expected, Boolean.parseBoolean(selectable[i]));
                return;
            }
        }
        fail("Missing pile card " + blueprint);
    }
    @Test public void SkullAndMandalorianMishapUploadsFallenPortalButNotBlastTheDoorKid() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var portal = scn.GetLSCard("portal");
        var blast = scn.GetLSCard("blast");
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(portal, blast);
        scn.LSPlayCard(combo, "Take card");
        scn.PassAllResponses();
        assertSelectable(scn, portal.getBlueprintId(true), true);
        assertSelectable(scn, blast.getBlueprintId(true), false);
        scn.LSChooseCard(portal);
        scn.PassAllResponses();
        assertTrue(scn.GetLSHand().contains(portal));
        assertTrue(scn.GetLSReserveDeck().contains(blast));
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void SkullAndMandalorianMishapPlacesNonDroidOutOfPlayToAddPower() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var body = scn.GetLSCard("obiwan");
        var droid = scn.GetLSCard("droid");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToTopOfLSLostPile(body, droid);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSFiller(1));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        int power = scn.GetLSTotalPower();
        scn.LSPlayCard(combo, "Place card");
        scn.PassAllResponses();
        assertSelectable(scn, body.getBlueprintId(true), true);
        assertSelectable(scn, droid.getBlueprintId(true), false);
        scn.LSChooseCard(body);
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, body.getZone());
        assertEquals(power + body.getBlueprint().getDestiny(), scn.GetLSTotalPower(), scn.epsilon);
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void SkullAndMandalorianMishapReducesDefendingBobasPowerAndAttrition() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("boba"), scn.GetDSFiller(1));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(3);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(6);
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToEndOfPowerSegment(true);
        int power = scn.GetDSTotalPower();
        assertEquals(6, scn.GetUnpaidLSAttrition());
        if (scn.LSGetDecision() == null) scn.DSPass();
        scn.LSPlayCard(combo, "Reduce opponent");
        scn.PassCardPlayResponses();
        scn.PassDestinyDrawResponses();
        assertEquals(3, scn.GetUnpaidLSAttrition());
        assertEquals(power - 3, scn.GetDSTotalPower());
        assertTrue(scn.GetLSUsedPile().contains(combo));
    }
}
