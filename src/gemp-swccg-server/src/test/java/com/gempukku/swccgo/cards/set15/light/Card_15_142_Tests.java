package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_142_Tests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("interrupt", "15_142");
        light.put("luke", "1_19");
        light.put("speeder", "3_66");
        var dark = new HashMap<String, String>();
        dark.put("crawler", "1_309");
        return new VirtualTableScenario(light, dark);
    }
    @Test
    public void UnderAttackStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("interrupt").getBlueprint();
        assertEquals("Under Attack", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(6f, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertTrue(card.isHorizontal());
        assertEquals("", card.getLore());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        scn.BlueprintIconCheck(card, List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of());
        scn.BlueprintPersonaCheck(card, List.of());
    }
    private void attack(boolean aboard, int destiny, boolean destroyed) {
        var scn = scenario();
        var interrupt = scn.GetLSCard("interrupt");
        var luke = scn.GetLSCard("luke");
        var speeder = scn.GetLSCard("speeder");
        var crawler = scn.GetDSCard("crawler");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(interrupt);
        scn.MoveCardsToLocation(site, luke, crawler, speeder);
        // Transport drivers use the engine's pilot-capacity slot.
        scn.BoardAsPilot(crawler, scn.GetDSFiller(1));
        if (aboard) scn.BoardAsPassenger(speeder, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(3, scn.GetArmor(crawler));
        scn.PrepareLSDestiny(destiny);
        scn.LSPlayCard(interrupt);
        scn.LSChooseCard(crawler);
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        // The destroyed vehicle and its driver require an explicit Lost Pile ordering decision.
        if (destroyed) {
            assertTrue(scn.DSDecisionAvailable("Choose card to put on Lost Pile"));
            scn.DSChooseCard(crawler);
            scn.PassAllResponses();
            if (scn.DSDecisionAvailable("Choose card to put on Lost Pile")) {
                scn.DSChooseCard(scn.GetDSFiller(1));
                scn.PassAllResponses();
            }
        }
        assertEquals(destroyed, scn.GetDSLostPile().contains(crawler));
        assertTrue(scn.GetLSUsedPile().contains(interrupt));
    }
    @Test public void UnderAttackWarriorAboardEnclosedVehicleCanTargetAtSameSite() { attack(true, 4, true); }
    @Test public void UnderAttackDestinyEqualToArmorDoesNotDestroyVehicle() { attack(false, 3, false); }
}
