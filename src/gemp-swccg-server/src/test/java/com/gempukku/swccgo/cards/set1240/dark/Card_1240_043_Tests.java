package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_043_Tests {
    private static final String SEARCH = "Take card into hand from Reserve Deck";

    private VirtualTableScenario Scenario(boolean conference, StartingSetup starting) {
        var ds = new HashMap<>(Map.of("cylinder", "1240_43", "station", "4_160", "beacon", "7_217", "kessel", "1_288",
                "mantell", "3_151", "war", "1_287", "try", "4_134", "hide", "9_133", "endor", "9_124", "plans", "7_240"));
        ds.put("rack", "1_211");
        return new VirtualTableScenario(new HashMap<>(), ds, 38, 38, StartingSetup.LSStartingLocation("8_70"),
                conference ? StartingSetup.DSStartingLocation("2_144") : StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt, starting, StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
    }

    @Test
    public void ImperialCodeCylinderMatchesTheBlockFourPrinting() {
        var scn = Scenario(false, StartingSetup.DeclineDSStartingInterrupt);
        var card = scn.GetDSCard("cylinder").getBlueprint();
        assertEquals("Imperial Code Cylinder", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.LEGACY_BLOCK_4)));
    }

    private boolean IsSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.DS, "blueprintId");
        var selectable = scn.GetADParam(scn.DS, "selectable");
        assertNotNull(blueprints);
        assertEquals(blueprints.length, selectable.length);
        for (int i = 0; i < blueprints.length; i++) {
            if (blueprints[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(selectable[i])) return true;
        }
        return false;
    }

    private VirtualTableScenario UsedReady() {
        var scn = Scenario(false, StartingSetup.DeclineDSStartingInterrupt);
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("cylinder"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("station"), scn.GetDSCard("beacon"), scn.GetDSCard("kessel"), scn.GetDSCard("war"));
        scn.DrainDSForcePileTo(0);
        // Advance a phase so the pending action decision reflects the cheats above.
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void SearchFor(String name) {
        var scn = UsedReady();
        var cylinder = scn.GetDSCard("cylinder");
        var selected = scn.GetDSCard(name);
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        remaining.remove(selected);
        scn.gameState().setRandomSeed(124043);
        scn.DSPlayCard(cylinder, SEARCH);
        scn.PassAllResponses();
        assertTrue(IsSelectable(scn, scn.GetDSCard("station")));
        assertTrue(IsSelectable(scn, scn.GetDSCard("beacon")));
        assertTrue(IsSelectable(scn, scn.GetDSCard("kessel")));
        assertFalse(IsSelectable(scn, scn.GetDSCard("war")));
        assertFalse(IsSelectable(scn, scn.GetDSCard("rack")));
        scn.DSChooseCard(selected);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, selected);
        assertInZone(Zone.USED_PILE, cylinder);
        for (var other : List.of("station", "beacon", "kessel")) {
            if (!name.equals(other)) assertInZone(Zone.RESERVE_DECK, scn.GetDSCard(other));
        }
        assertEquals(0, scn.GetDSForcePileCount());
        assertEquals(remaining.size(), scn.GetDSReserveDeckCount());
        assertNotEquals(remaining, new ArrayList<>(scn.GetDSReserveDeck()));
    }

    @Test public void UsedTakesOriginalExecutorControlStationAndReshuffles() { SearchFor("station"); }
    @Test public void UsedTakesOriginalHomingBeaconAndReshuffles() { SearchFor("beacon"); }
    @Test public void UsedTakesOriginalKesselAndReshuffles() { SearchFor("kessel"); }

    @Test
    public void UsedSearchWithoutASelectionReshufflesAndStillPlacesCylinderInUsed() {
        var scn = UsedReady();
        scn.MoveCardsToDSHand(scn.GetDSCard("station"), scn.GetDSCard("beacon"), scn.GetDSCard("kessel"));
        var before = new ArrayList<>(scn.GetDSReserveDeck());
        scn.gameState().setRandomSeed(124043);
        var cylinder = scn.GetDSCard("cylinder");
        scn.DSPlayCard(cylinder, SEARCH);
        scn.PassAllResponses();
        assertFalse(IsSelectable(scn, scn.GetDSCard("war")));
        scn.DSPass();
        scn.PassAllResponses();
        while (scn.DSDecisionAvailable("Verify Reserve Deck") || scn.LSDecisionAvailable("Verify Reserve Deck")) {
            if (scn.DSDecisionAvailable("Verify Reserve Deck")) scn.DSPass();
            else scn.LSPass();
        }
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, cylinder);
        assertEquals(before.size(), scn.GetDSReserveDeckCount());
        assertNotEquals(before, new ArrayList<>(scn.GetDSReserveDeck()));
    }

    private void PlayStartingCylinder(VirtualTableScenario scn) {
        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        scn.DSChooseCard(scn.GetDSCard("cylinder"));
        // Ord Mantell, War Room and There Is No Try may be auto-chosen when they are the only match.
        var effects = new java.util.HashSet<>(List.of("hide", "endor"));
        int search = 0;
        for (int step = 0; step < 45 && !scn.AwaitingDSActivatePhaseActions() && !scn.AwaitingLSActivatePhaseActions(); step++) {
            if (scn.DSDecisionAvailable("to deploy from Reserve Deck")) {
                search++;
                assertFalse(IsSelectable(scn, scn.GetDSCard("rack")));
                String pick = null;
                for (var key : List.of("mantell", "war", "try", "hide", "endor")) {
                    if (IsSelectable(scn, scn.GetDSCard(key))) { pick = key; break; }
                }
                assertNotNull("Expected a printed download choice", pick);
                if (effects.contains(pick)) assertTrue(IsSelectable(scn, scn.GetDSCard("plans")));
                scn.DSChooseCard(scn.GetDSCard(pick));
            } else if (scn.DSDecisionAvailable("On which side")) {
                scn.DSChoose("Left");
            } else if (scn.DSDecisionAvailable("Choose where to deploy")) {
                assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
                scn.DSChooseCard(scn.GetLSStartingLocation());
            } else if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional response")) {
                scn.PassAllResponses();
            } else {
                fail("Unexpected native starting decision: " + scn.GetCurrentDecision().getText());
            }
        }
        assertTrue(search >= 2);
    }

    @Test
    public void StartingWithConferenceRoomDeploysThreeNamedCardsAndExactlyTwoImmuneEffects() {
        StartingSetup start = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(); }
            @Override public void Setup(VirtualTableScenario scn) { PlayStartingCylinder(scn); }
        };
        var scn = Scenario(true, start);
        scn.StartGame();
        assertInZone(Zone.LOCATIONS, scn.GetDSCard("mantell"));
        assertInZone(Zone.LOCATIONS, scn.GetDSCard("war"));
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("try"));
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("hide"));
        assertInZone(Zone.ATTACHED, scn.GetDSCard("endor"));
        assertEquals(scn.GetLSStartingLocation(), scn.GetDSCard("endor").getAttachedTo());
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("plans"));
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("rack"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("cylinder"));
    }

    @Test
    public void StartingCylinderIsNotOfferedWithoutConferenceRoom() {
        StartingSetup start = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(); }
            @Override public void Setup(VirtualTableScenario scn) { assertFalse(scn.DSDecisionAvailable("Choose starting interrupt")); }
        };
        var scn = Scenario(false, start);
        scn.StartGame();
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("cylinder"));
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("mantell"));
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("war"));
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("try"));
    }
}
