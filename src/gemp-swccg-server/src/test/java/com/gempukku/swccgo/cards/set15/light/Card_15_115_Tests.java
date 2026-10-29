package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_115_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("combo", "15_115");
        cards.put("chewie", "15_3");
        cards.put("lando", "5_5");
        cards.put("han", "1_14");
        var dark = new HashMap<String,String>();
        dark.put("vader", "1_168");
        return new VirtualTableScenario(cards, dark);
    }
    @Test
    public void OldBenAndNobleSacrificeStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Old Ben & Noble Sacrifice", card.getTitle());
        assertEquals(List.of("Old Ben", "Noble Sacrifice"), card.getTitles());
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
    private void reachPlayWindow(VirtualTableScenario scn) {
        var combo = scn.GetLSCard("combo");
        for (int i = 0; i < 24; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(combo)) return;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        fail("Expected the printed just-deployed or just-forfeited response window");
    }
    @Test public void OldBenAndNobleSacrificeSacrificesMatchingPowerAndRetrievesForfeit() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var sacrifice = scn.GetLSFiller(1);
        var deployed = scn.GetDSFiller(1);
        var first = scn.GetLSFiller(2);
        var second = scn.GetLSFiller(3);
        scn.StartGame();
        scn.MoveCardsToHand(combo, deployed);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), sacrifice);
        scn.MoveCardsToTopOfLSLostPile(first, second);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(scn.GetPower(sacrifice), scn.GetPower(deployed));
        assertEquals(2, scn.GetForfeit(sacrifice));
        scn.DSPlayCard(deployed);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        reachPlayWindow(scn);
        scn.LSPlayCard(combo, "Sacrifice");
        scn.LSChooseCard(deployed);
        scn.LSChooseCard(sacrifice);
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, sacrifice.getZone());
        assertTrue(scn.GetLSUsedPile().contains(first));
        assertTrue(scn.GetLSUsedPile().contains(second));
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void OldBenAndNobleSacrificeRevivesForfeitedCharacterAtSameTatooineSite() {
        var scn = scenario();
        var combo = scn.GetLSCard("combo");
        var trooper = scn.GetLSFiller(1);
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, trooper, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToDamageSegment(false);
        scn.LSChooseCard(trooper);
        reachPlayWindow(scn);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, "Revive");
        scn.PassAllResponses();
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(site, trooper));
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
