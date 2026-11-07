package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_042_Tests {
    private static final String CHOOSE_PILOT = "Choose Bossk, Dengar";

    private VirtualTableScenario scenario(Map<String, String> darkCards) {
        var cards = new HashMap<>(darkCards);
        cards.put("interrupt", "1261_42");
        return new VirtualTableScenario(new HashMap<>(), cards, 24, 24,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private void playStartingInterrupt(VirtualTableScenario scn) {
        scn.StartGame(false);
        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        scn.DSChooseCard(scn.GetDSCard("interrupt"));
        if (scn.LSDecisionAvailable("Choose starting interrupt"))
            scn.LSPass();
        passResponses(scn);
    }

    private void passResponses(VirtualTableScenario scn) {
        for (int i = 0; i < 8; i++) {
            if (scn.LSDecisionAvailable("Optional"))
                scn.LSPass();
            else if (scn.DSDecisionAvailable("Optional"))
                scn.DSPass();
            else
                return;
        }
        fail("Response window did not resolve");
    }

    private boolean selectable(VirtualTableScenario scn, String key) {
        assertTrue("Expected live selection decision", scn.DSDecisionAvailable(CHOOSE_PILOT));
        var ids = scn.DSGetADParamAsList("cardId");
        int index = ids.indexOf(Integer.toString(scn.GetDSCard(key).getCardId()));
        assertTrue("Card is missing from the Reserve Deck choice: " + key, index >= 0);
        return Boolean.parseBoolean(scn.DSGetADParamAsList("selectable").get(index));
    }

    private void select(VirtualTableScenario scn, String... keys) {
        assertTrue("Expected live selection decision", scn.DSDecisionAvailable(CHOOSE_PILOT));
        scn.DSChooseCards(java.util.Arrays.stream(keys).map(scn::GetDSCard)
                .toArray(com.gempukku.swccgo.game.PhysicalCardImpl[]::new));
    }

    private void assertStartingHand(VirtualTableScenario scn, String... picks) {
        passResponses(scn);
        assertEquals(4 + picks.length, scn.GetDSHandCount());
        assertEquals(8, scn.GetLSHandCount());
        for (String pick : picks)
            assertEquals(Zone.HAND, scn.GetDSCard(pick).getZone());
        assertEquals(Zone.LOST_PILE, scn.GetDSCard("interrupt").getZone());
    }

    @Test
    public void printedIdentity() {
        var scn = scenario(Map.of());
        var card = scn.GetDSCard("interrupt").getBlueprint();
        assertEquals("Lone Pilot", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new java.util.ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(card, new java.util.ArrayList<>(List.of(Icon.INTERRUPT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(card, new java.util.ArrayList<>());
    }

    @Test
    public void eachPrintedPilotQualifiesOnItsOwn() {
        for (String id : List.of("4_92", "4_100", "1_173", "10_41", "601_89")) {
            var scn = scenario(Map.of("pilot", id));
            playStartingInterrupt(scn);
            assertTrue("Named pilot must qualify: " + id, selectable(scn, "pilot"));
            select(scn, "pilot");
            assertStartingHand(scn, "pilot");
        }
    }

    @Test
    public void mayStopAfterOnePilotDespiteAvailableOptions() {
        var scn = scenario(Map.of("bossk", "4_92", "dengar", "4_100", "system", "1_282"));
        playStartingInterrupt(scn);
        assertTrue(selectable(scn, "bossk"));
        assertTrue(selectable(scn, "dengar"));
        assertFalse(selectable(scn, "system"));
        select(scn, "bossk");
        assertFalse(selectable(scn, "dengar"));
        assertTrue(selectable(scn, "system"));
        select(scn, "bossk"); // Done: retain exactly the current legal combination.
        assertStartingHand(scn, "bossk");
    }

    @Test
    public void addsOneOfEachOptionalCardAndRejectsNonmatchingOrIneligibleCards() {
        var scn = scenario(Map.ofEntries(
                Map.entry("bossk", "4_92"), Map.entry("vader", "1_168"),
                Map.entry("oneIcon", "1_282"), Map.entry("otherOneIcon", "3_151"),
                Map.entry("twoIcons", "8_156"), Map.entry("site", "1_293"),
                Map.entry("matching", "4_168"), Map.entry("wrongShip", "4_169"),
                Map.entry("always", "7_234"), Map.entry("alsoAlways", "10_52"),
                Map.entry("conditional", "15_171"), Map.entry("plain", "1_225")));
        playStartingInterrupt(scn);
        assertFalse(selectable(scn, "vader"));
        assertFalse(selectable(scn, "matching"));
        assertFalse(selectable(scn, "always"));
        select(scn, "bossk");
        assertTrue(selectable(scn, "oneIcon"));
        assertTrue(selectable(scn, "otherOneIcon"));
        assertFalse(selectable(scn, "twoIcons"));
        assertFalse(selectable(scn, "site"));
        assertTrue(selectable(scn, "matching"));
        assertFalse(selectable(scn, "wrongShip"));
        assertTrue(selectable(scn, "always"));
        assertFalse(selectable(scn, "conditional"));
        assertFalse(selectable(scn, "plain"));
        select(scn, "bossk", "oneIcon");
        assertFalse(selectable(scn, "otherOneIcon"));
        select(scn, "bossk", "oneIcon", "always");
        assertFalse(selectable(scn, "alsoAlways"));
        select(scn, "bossk", "oneIcon", "always", "matching");
        assertStartingHand(scn, "bossk", "oneIcon", "always", "matching");
    }

    @Test
    public void noMandatoryPilotReturnsInterruptAndKeepsNormalDraw() {
        var scn = scenario(Map.of("wrongPilot", "1_194", "system", "1_282", "effect", "7_234"));
        playStartingInterrupt(scn);
        assertFalse(scn.DSDecisionAvailable(CHOOSE_PILOT));
        assertEquals(8, scn.GetDSHandCount());
        assertEquals(8, scn.GetLSHandCount());
        var zone = scn.GetDSCard("interrupt").getZone();
        assertTrue(zone == Zone.RESERVE_DECK || zone == Zone.HAND);
        assertNull(scn.gameState().getStartingInterruptPlayed(scn.DS));
    }

    @Test
    public void usedActivatesExactlyOneForceAndCyclesInterrupt() {
        var scn = scenario(Map.of());
        var interrupt = scn.GetDSCard("interrupt");
        scn.StartGame();
        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        scn.DSPass();
        scn.MoveCardsToDSHand(interrupt);
        scn.SkipToDSTurn(Phase.CONTROL);
        int force = scn.GetDSForcePileCount();
        int reserve = scn.GetDSReserveDeckCount();
        assertTrue(scn.DSCardPlayAvailable(interrupt, "Activate 1 Force"));
        scn.DSPlayCard(interrupt, "Activate 1 Force");
        passResponses(scn);
        assertEquals(force + 1, scn.GetDSForcePileCount());
        assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
        assertEquals(Zone.USED_PILE, interrupt.getZone());
    }
}
