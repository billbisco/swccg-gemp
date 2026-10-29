package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_028_Tests {
    private VirtualTableScenario scenario() {
        var cards = new HashMap<String, String>();
        cards.put("effect", "15_28");
        cards.put("scout", "3_16");
        cards.put("scout2", "3_16");
        cards.put("blaster", "1_152");
        cards.put("blaster2", "1_152");
        cards.put("system", "1_127");
        cards.put("tala", "9_84");
        cards.put("pilot", "1_19");
        return new VirtualTableScenario(cards, new HashMap<>());
    }
    @Test
    public void CloseAirSupportAndCommandoTrainingStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("effect").getBlueprint();
        assertEquals("Close Air Support & Commando Training", card.getTitle());
        assertEquals(List.of("Close Air Support", "Commando Training"), card.getTitles());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        assertEquals(3f, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertFalse(card.isHorizontal());
        assertEquals("", card.getLore());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.EFFECT));
        scn.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
    }
    @Test
    public void CloseAirSupportAndCommandoTrainingArmedRebelsAddForfeitAndCumulativeDestiny() {
        var scn = scenario();
        var effect = scn.GetLSCard("effect");
        var scout = scn.GetLSCard("scout");
        var second = scn.GetLSCard("scout2");
        var blaster = scn.GetLSCard("blaster");
        var blaster2 = scn.GetLSCard("blaster2");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(effect, blaster, blaster2);
        scn.MoveCardsToLocation(site, scout, second, scn.GetDSFiller(1));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(effect);
        scn.PassAllResponses();
        int base = scn.GetForfeit(scout);
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), effect, Title.Alter));
        scn.AttachCardsTo(scout, blaster);
        scn.AttachCardsTo(second, blaster2);
        assertEquals(base + 2, scn.GetForfeit(scout));
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), effect, Title.Alter));
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(2f, scn.game().getModifiersQuerying().getTotalBattleDestiny(scn.gameState(), scn.LS, 0), scn.epsilon);
    }
    @Test
    public void CloseAirSupportAndCommandoTrainingTalaAddsBattleDestinyOncePerTurn() {
        var scn = scenario();
        var effect = scn.GetLSCard("effect");
        var system = scn.GetLSCard("system");
        var tala = scn.GetLSCard("tala");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(effect);
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, tala);
        scn.BoardAsPilot(tala, scn.GetLSCard("pilot"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("scout"), scn.GetDSFiller(1));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(effect);
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        int before = scn.GetLSBattleDestinyCount();
        assertTrue(scn.LSCardActionAvailable(effect, "Add one battle destiny and one destiny to power"));
        scn.LSUseCardAction(effect, "Add one battle destiny and one destiny to power");
        scn.PassAllResponses();
        assertEquals(before + 1, scn.GetLSBattleDestinyCount());
        scn.DSPass(); // Give Light Side the next weapons-segment action opportunity.
        assertFalse(scn.LSCardActionAvailable(effect, "Add one battle destiny"));
    }
}
