package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_15_120_Tests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("interrupt", "15_120");
        light.put("luke", "1_19");
        light.put("speeder", "3_66");
        var dark = new HashMap<String, String>();
        dark.put("crawler", "1_309");
        dark.put("probe", "3_90");
        dark.put("mine", "3_160");
        return new VirtualTableScenario(light, dark);
    }
    @Test
    public void PerimeterScanAndUnderAttackStatsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("interrupt").getBlueprint();
        assertEquals("Perimeter Scan & Under Attack", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
    @Test public void PerimeterScanAndUnderAttackDestroysVehicleWhenDestinyExceedsArmor() { attack(false, 4, true); }
    @Test public void PerimeterScanAndUnderAttackCannotUseWarriorAboardEnclosedVehicle() {
        var scn=scenario(); scn.StartGame(); var speeder=scn.GetLSCard("speeder");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), speeder, scn.GetDSCard("crawler"));
        scn.BoardAsPassenger(speeder, scn.GetLSCard("luke")); scn.MoveCardsToHand(scn.GetLSCard("interrupt"));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("interrupt")));
    }
    @Test public void PerimeterScanAndUnderAttackPeeksAtFaceDownCardForOneForceWithoutMovingIt() {
        var scn=scenario(); scn.StartGame(); var card=scn.GetLSCard("interrupt"); var hidden=scn.GetDSFiller(1);
        scn.StackCardsFaceDownOn(scn.GetDSStartingLocation(), hidden); scn.MoveCardsToHand(card); scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL); int force=scn.GetLSForcePileCount();
        scn.LSPlayCard(card,"Peek at face-down"); scn.LSChooseCard(hidden); scn.PassAllResponses(); scn.LSDecided(""); scn.PassAllResponses();
        assertTrue(com.gempukku.swccgo.filters.Filters.face_down.accepts(scn.game(), hidden)); assertEquals(scn.GetDSStartingLocation(), hidden.getStackedOn());
        assertEquals(force-1,scn.GetLSForcePileCount()); assertTrue(scn.GetLSUsedPile().contains(card));
    }
    @Test public void PerimeterScanAndUnderAttackPeeksAtHandAndPlacesOnlyDroidSpiesAndMinesInUsedPile() {
        var scn=scenario(); scn.StartGame(); var card=scn.GetLSCard("interrupt"); var probe=scn.GetDSCard("probe"); var mine=scn.GetDSCard("mine"); var other=scn.GetDSFiller(1);
        scn.MoveCardsToHand(card,probe,mine,other); scn.EnsureLSForcePile(5); scn.SkipToLSTurn(Phase.CONTROL);
        int force=scn.GetLSForcePileCount(); scn.LSPlayCard(card,"Peek at opponent"); scn.PassAllResponses(); scn.LSDecided(""); scn.PassAllResponses();
        scn.LSChooseCard(probe); scn.PassAllResponses(); scn.LSChooseCard(mine); scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose card to put on Used Pile")); scn.LSDecided(""); scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(probe)); assertTrue(scn.GetDSUsedPile().contains(mine)); assertTrue(scn.GetDSHand().contains(other));
        assertEquals(force-1,scn.GetLSForcePileCount()); assertTrue(scn.GetLSUsedPile().contains(card));
    }
    @Test public void PerimeterScanAndUnderAttackDestinyEqualToArmorDoesNotDestroyVehicle() { attack(false, 3, false); }
}
