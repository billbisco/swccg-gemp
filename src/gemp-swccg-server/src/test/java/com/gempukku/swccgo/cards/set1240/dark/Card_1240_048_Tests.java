package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_048_Tests {
    private static final String SEARCH = "Take Stinger or Virago into hand";
    private static final String PROTECT = "Protect Stinger and Virago from weapons";

    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("corvette", "1_140"); put("red1", "1_144"); put("luke", "1_19"); put("cannon", "1_159");
            put("spin", "7_85"); put("signal", "6_77"); put("planners", "1_60");
            put("corvette2", "1_140"); put("cannon2", "1_159");
        }}, new HashMap<>() {{
            put("rebels", "1240_48"); put("stinger", "10_49"); put("virago", "10_54");
            put("dread", "106_13"); put("sector", "4_155");
        }}, 40, 40, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ThoseRebelsWontEscapeUsPrintedIdentityIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("rebels").getBlueprint();
        assertEquals(Title.Those_Rebels_Wont_Escape_Us, bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
    }

    private VirtualTableScenario Ready(boolean light, Phase phase) {
        var scn = GetScenario();
        scn.StartGame();
        if (light) scn.SkipToLSTurn(Phase.ACTIVATE);
        else scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.EnsureLSForcePile(10);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToDSHand(scn.GetDSCard("rebels"));
        scn.SkipToPhase(phase);
        return scn;
    }

    private boolean IsSelectable(VirtualTableScenario scn, String player, PhysicalCardImpl card) {
        String[] ids = scn.GetADParam(player, "cardId");
        String[] blueprints = scn.GetADParam(player, "blueprintId");
        String[] selectable = scn.GetADParam(player, "selectable");
        assertNotNull(selectable);
        if (ids != null) {
            assertEquals(ids.length, selectable.length);
            for (int i = 0; i < ids.length; i++) {
                if (ids[i].equals(String.valueOf(card.getCardId()))) return Boolean.parseBoolean(selectable[i]);
            }
        }
        if (blueprints != null) {
            assertEquals(blueprints.length, selectable.length);
            for (int i = 0; i < blueprints.length; i++) {
                if (blueprints[i].equals(card.getBlueprintId(true))) return Boolean.parseBoolean(selectable[i]);
            }
        }
        return false;
    }

    private void SearchFor(String name) {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("dread"), scn.GetDSCard("virago"), scn.GetDSCard("stinger"));
        scn.EnsureDSForcePile(1);
        int reserve = scn.GetDSReserveDeckCount();
        var order = new ArrayList<>(scn.GetDSReserveDeck());
        order.remove(scn.GetDSCard(name));
        scn.gameState().setRandomSeed(124048);
        scn.DSPlayCard(scn.GetDSCard("rebels"), SEARCH);
        scn.PassAllResponses();
        assertTrue(IsSelectable(scn, scn.DS, scn.GetDSCard("stinger")));
        assertTrue(IsSelectable(scn, scn.DS, scn.GetDSCard("virago")));
        assertFalse(IsSelectable(scn, scn.DS, scn.GetDSCard("dread")));
        scn.DSChooseCard(scn.GetDSCard(name));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard(name));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("rebels"));
        assertEquals(0, scn.GetDSForcePileCount());
        assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
        assertNotEquals(order, new ArrayList<>(scn.GetDSReserveDeck()));
    }

    @Test public void ThoseRebelsWontEscapeUsActuallyTakesStingerForOneForceAndReshuffles() { SearchFor("stinger"); }
    @Test public void ThoseRebelsWontEscapeUsActuallyTakesViragoForOneForceAndReshuffles() { SearchFor("virago"); }

    @Test
    public void ThoseRebelsWontEscapeUsCannotSearchWithZeroActivatedForce() {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("stinger"));
        scn.DrainDSForcePileTo(0);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("rebels"), SEARCH));
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsCannotSearchAnEmptyReserveDeck() {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSReserveDeck().toArray(new PhysicalCardImpl[0]));
        assertEquals(0, scn.GetDSReserveDeckCount());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("rebels"), SEARCH));
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsFailedSearchStillPaysOneAndBecomesUsed() {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("stinger"), scn.GetDSCard("virago"));
        scn.EnsureDSForcePile(1);
        int reserve = scn.GetDSReserveDeckCount();
        scn.DSPlayCard(scn.GetDSCard("rebels"), SEARCH);
        scn.PassAllResponses();
        // A real hidden-deck search may fail; no eligible card is in Reserve.
        if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().contains("Choose")) scn.DSPass();
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSForcePileCount());
        assertEquals(reserve, scn.GetDSReserveDeckCount());
        assertInZone(Zone.USED_PILE, scn.GetDSCard("rebels"));
    }

    private VirtualTableScenario BattleReady(boolean light, boolean namesHere, boolean sector, boolean otherShip) {
        return BattleReady(light, namesHere, sector, otherShip, null);
    }

    private VirtualTableScenario BattleReady(boolean light, boolean namesHere, boolean sector, boolean otherShip, String onlyName) {
        var scn = Ready(light, Phase.CONTROL);
        var location = scn.GetDSStartingLocation();
        if (sector) {
            scn.MoveLocationToTable(scn.GetDSCard("sector"));
            location = scn.GetDSCard("sector");
        }
        scn.MoveCardsToLocation(location, scn.GetLSCard("corvette"), scn.GetLSCard("red1"));
        scn.BoardAsPilot(scn.GetLSCard("red1"), scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetLSCard("corvette"), scn.GetLSCard("cannon"));
        scn.MoveCardsToLocation(namesHere ? location : scn.GetLSStartingLocation(), scn.GetDSCard("stinger"), scn.GetDSCard("virago"));
        if (onlyName != null) scn.MoveCardsToDSHand(scn.GetDSCard(onlyName.equals("stinger") ? "virago" : "stinger"));
        if (otherShip) scn.MoveCardsToLocation(location, scn.GetDSCard("dread"));
        else scn.MoveCardsToDSHand(scn.GetDSCard("dread"));
        scn.SkipToPhase(Phase.BATTLE);
        if (light) scn.LSInitiateBattle(location);
        else scn.DSInitiateBattle(location);
        return scn;
    }

    private void ReachDS(VirtualTableScenario scn, String action) {
        for (int i = 0; i < 40; i++) {
            if (scn.DSAnyActionsAvailable() && scn.DSCardActionAvailable(scn.GetDSCard("rebels"), action)) return;
            assertNotNull(scn.GetCurrentDecision());
            assertTrue(scn.GetCurrentDecision().getText(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Expected interrupt action: " + action);
    }

    private void PassWithoutDSAction(VirtualTableScenario scn, String action) {
        for (int i = 0; i < 40; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) return;
            if (scn.DSAnyActionsAvailable()) assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("rebels"), action));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Response windows did not finish");
    }

    private void Protect(VirtualTableScenario scn) {
        ReachDS(scn, PROTECT);
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(scn.GetDSCard("rebels"), PROTECT);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetDSCard("rebels"));
        assertEquals(force, scn.GetDSForcePileCount());
    }

    private void ReachWeaponsSegment(VirtualTableScenario scn) {
        scn.PassAllResponses();
        assertTrue("Expected an actual weapons segment action decision",
                scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
    }

    private void FireCannon(VirtualTableScenario scn) {
        ReachWeaponsSegment(scn);
        for (int i = 0; i < 12; i++) {
            if (scn.LSAnyActionsAvailable() && scn.LSCardActionAvailable(scn.GetLSCard("cannon"), "Fire")) {
                scn.LSUseCardAction(scn.GetLSCard("cannon"), "Fire");
                return;
            }
            assertNotNull(scn.GetCurrentDecision());
            String text = scn.GetCurrentDecision().getText().toLowerCase();
            assertTrue(text, text.contains("weapon") || text.contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Expected real cannon firing action");
    }

    private void ProtectedWeaponTargets(boolean sector) {
        var scn = BattleReady(true, true, sector, true);
        Protect(scn);
        scn.PrepareLSDestiny(7);
        int force = scn.GetLSForcePileCount();
        FireCannon(scn);
        assertFalse(IsSelectable(scn, scn.LS, scn.GetDSCard("stinger")));
        assertFalse(IsSelectable(scn, scn.LS, scn.GetDSCard("virago")));
        assertTrue(IsSelectable(scn, scn.LS, scn.GetDSCard("dread")));
        scn.LSChooseCard(scn.GetDSCard("dread"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("dread").isHit());
        assertFalse(scn.GetDSCard("stinger").isHit());
        assertFalse(scn.GetDSCard("virago").isHit());
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test public void ThoseRebelsWontEscapeUsProtectsBothNamedFightersButNotOtherShipAtSystem() { ProtectedWeaponTargets(false); }
    @Test public void ThoseRebelsWontEscapeUsProtectsBothNamedFightersAtSector() { ProtectedWeaponTargets(true); }

    private void ProtectOnlyNamedFighter(String name) {
        var scn = BattleReady(true, true, false, true, name);
        Protect(scn);
        scn.PrepareLSDestiny(7);
        FireCannon(scn);
        assertFalse(IsSelectable(scn, scn.LS, scn.GetDSCard(name)));
        assertTrue(IsSelectable(scn, scn.LS, scn.GetDSCard("dread")));
        scn.LSChooseCard(scn.GetDSCard("dread"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("dread").isHit());
        assertFalse(scn.GetDSCard(name).isHit());
    }

    @Test public void ThoseRebelsWontEscapeUsProtectionRequiresOnlyStingerWithoutVirago() { ProtectOnlyNamedFighter("stinger"); }
    @Test public void ThoseRebelsWontEscapeUsProtectionRequiresOnlyViragoWithoutStinger() { ProtectOnlyNamedFighter("virago"); }

    @Test
    public void ThoseRebelsWontEscapeUsProtectionDoesNotRequireTheSearchModesOneForce() {
        var scn = BattleReady(true, true, false, true);
        scn.DrainDSForcePileTo(0);
        Protect(scn);
        scn.PrepareLSDestiny(7);
        FireCannon(scn);
        assertFalse(IsSelectable(scn, scn.LS, scn.GetDSCard("stinger")));
        scn.LSChooseCard(scn.GetDSCard("dread"));
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSCard("dread").isHit());
    }

    @Test
    public void ThoseRebelsWontEscapeUsMayDeclineProtectionAndStingerCanActuallyBeHit() {
        var scn = BattleReady(true, true, false, true);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(4);
        FireCannon(scn);
        assertTrue(IsSelectable(scn, scn.LS, scn.GetDSCard("stinger")));
        assertTrue(IsSelectable(scn, scn.LS, scn.GetDSCard("virago")));
        scn.LSChooseCard(scn.GetDSCard("stinger"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("stinger").isHit());
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsCannotProtectWhenOwnerInitiatesBattle() {
        var scn = BattleReady(false, true, false, true);
        PassWithoutDSAction(scn, PROTECT);
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsCannotProtectWhenNamedFightersAreAtAnotherSystem() {
        var scn = BattleReady(true, false, false, true);
        PassWithoutDSAction(scn, PROTECT);
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsCannotWaitUntilWeaponsSegmentToPlayProtection() {
        var scn = BattleReady(true, true, false, true);
        scn.PassAllResponses();
        ReachWeaponsSegment(scn);
        if (scn.DSAnyActionsAvailable()) assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("rebels"), PROTECT));
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsProtectionExpiresAtEndOfTurnBeforeNextBattle() {
        var scn = BattleReady(true, true, false, false);
        Protect(scn);
        scn.SkipToDamageSegment(false);
        // With no normal destiny drawn, original fighters7 face Corvette5 + piloted Red1(6).
        scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.MOVE);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        scn.PrepareLSDestiny(4);
        FireCannon(scn);
        assertTrue(IsSelectable(scn, scn.LS, scn.GetDSCard("stinger")));
        scn.LSChooseCard(scn.GetDSCard("stinger"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("stinger").isHit());
    }

    private VirtualTableScenario SpinReady() {
        var scn = BattleReady(true, true, false, true);
        scn.MoveCardsToLSHand(scn.GetLSCard("spin"));
        scn.PassAllResponses();
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(1);
        scn.SkipToPowerSegment();
        for (int i = 0; i < 80; i++) {
            if (scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(scn.GetLSCard("spin"))) {
                scn.LSPlayCard(scn.GetLSCard("spin"));
                if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("red1"))) scn.LSChooseCard(scn.GetLSCard("red1"));
                return scn;
            }
            if (scn.LSDecisionAvailable("battle destiny?")) scn.LSChooseYes();
            else if (scn.DSDecisionAvailable("battle destiny?")) scn.DSChooseYes();
            else {
                assertNotNull(scn.GetCurrentDecision());
                assertTrue(scn.GetCurrentDecision().getText(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        throw new AssertionError("Actual Darklighter Spin response was not reached");
    }

    @Test
    public void ThoseRebelsWontEscapeUsActuallyCancelsDarklighterSpinAtNoForceCost() {
        var scn = SpinReady();
        scn.DrainDSForcePileTo(0);
        ReachDS(scn, "Cancel");
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(scn.GetDSCard("rebels"), "Cancel");
        scn.PassAllResponses();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(1, scn.GetLSTotalDestiny());
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("spin"));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("rebels"));
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void ThoseRebelsWontEscapeUsMayDeclineCancellationAndDarklighterSpinStillSubstitutes() {
        var scn = SpinReady();
        scn.PassAllResponses();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(4, scn.GetLSTotalDestiny());
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsCannotCancelADifferentOriginalInterrupt() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSCard("signal"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("planners"));
        scn.LSPlayCard(scn.GetLSCard("signal"));
        PassWithoutDSAction(scn, "Cancel");
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSCard("planners"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetLSCard("planners"));
        assertInZone(Zone.HAND, scn.GetDSCard("rebels"));
    }

    @Test
    public void ThoseRebelsWontEscapeUsProtectionPersistsIntoAnotherBattleDuringSameTurn() {
        var scn = Ready(true, Phase.CONTROL);
        var first = scn.GetDSStartingLocation();
        var second = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(first, scn.GetLSCard("corvette"), scn.GetLSCard("red1"), scn.GetDSCard("stinger"));
        scn.BoardAsPilot(scn.GetLSCard("red1"), scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetLSCard("corvette"), scn.GetLSCard("cannon"));
        scn.MoveCardsToLocation(second, scn.GetLSCard("corvette2"), scn.GetDSCard("virago"), scn.GetDSCard("dread"));
        scn.AttachCardsTo(scn.GetLSCard("corvette2"), scn.GetLSCard("cannon2"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(first);
        Protect(scn);
        scn.SkipToDamageSegment(false);
        scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses();
        for (int i = 0; i < 12 && !scn.AwaitingLSBattlePhaseActions(); i++) scn.PlayerPass(scn.GetDecidingPlayer());
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSInitiateBattle(second);
        scn.PassAllResponses();
        ReachWeaponsSegment(scn);
        if (scn.DSAnyDecisionsAvailable()) {
            assertTrue(scn.DSGetDecision().getText().toLowerCase().contains("weapon"));
            scn.DSPass();
        }
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("cannon2"), "Fire"));
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(scn.GetLSCard("cannon2"), "Fire");
        assertFalse(IsSelectable(scn, scn.LS, scn.GetDSCard("virago")));
        assertTrue(IsSelectable(scn, scn.LS, scn.GetDSCard("dread")));
        scn.LSChooseCard(scn.GetDSCard("dread"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("dread").isHit());
        assertFalse(scn.GetDSCard("virago").isHit());
    }
}
