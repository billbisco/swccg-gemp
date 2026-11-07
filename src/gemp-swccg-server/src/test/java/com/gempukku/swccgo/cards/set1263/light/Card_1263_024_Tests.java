package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1263_024_Tests {
    private VirtualTableScenario fixture() {
        return new VirtualTableScenario(new HashMap<>(Map.of("rogue", "1263_24", "xwing", "1_145",
                "speeder", "3_69", "falcon", "1_143", "hoth", "3_55", "from", "3_56", "to", "3_62", "ordMantell", "3_64")),
                new HashMap<>(), 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void printedIdentityAndStats() {
        var scn = fixture(); var bp = scn.GetLSCard("rogue").getBlueprint();
        assertEquals("Veteran Rogue", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.LIGHT, bp.getSide()); assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon); assertEquals(1, bp.getDeployCost(), scn.epsilon);
        assertEquals(0, bp.getPower(), scn.epsilon); assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(2, bp.getForfeit(), scn.epsilon);
        assertEquals(Rarity.V, bp.getRarity()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.REBEL, Icon.PILOT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    private void pilot(String target, boolean ground, int maneuverBonus) {
        var scn = fixture(); scn.StartGame();
        var location = scn.GetLSCard(ground ? "from" : "hoth");
        var ship = scn.GetLSCard(target); var rogue = scn.GetLSCard("rogue");
        scn.MoveLocationToTable(location); scn.MoveCardsToLocation(location, ship);
        scn.BoardAsPilot(ship, rogue);
        assertEquals(ship.getBlueprint().getPower() + 2, scn.GetPower(ship), scn.epsilon);
        assertEquals(ship.getBlueprint().getManeuver() + maneuverBonus, scn.GetManeuver(ship), scn.epsilon);
    }

    @Test public void grantsPowerAndManeuverToXWing() { pilot("xwing", false, 2); }
    @Test public void grantsPowerAndManeuverToT47() { pilot("speeder", true, 2); }
    @Test public void grantsPowerButNotManeuverToFalcon() { pilot("falcon", false, 0); }

    @Test public void actualT47MoveSpendsNoForce() {
        var scn = fixture(); scn.StartGame();
        var from = scn.GetLSCard("from"); var to = scn.GetLSCard("to");
        var speeder = scn.GetLSCard("speeder"); var rogue = scn.GetLSCard("rogue");
        scn.MoveLocationToTable(from); scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, speeder); scn.BoardAsPilot(speeder, rogue);
        scn.SkipToLSTurn(Phase.MOVE); scn.EnsureLSForcePile(5);
        int before = scn.GetLSForcePile().size();
        scn.LSMoveCard(speeder, to); scn.PassAllResponses();
        assertEquals(to, speeder.getAtLocation());
        assertEquals(before, scn.GetLSForcePile().size());
    }
    private void actualStarshipMove(String key, int expectedCost) {
        var scn = fixture(); scn.StartGame();
        var from = scn.GetLSCard("hoth"); var to = scn.GetLSCard("ordMantell");
        var ship = scn.GetLSCard(key); var rogue = scn.GetLSCard("rogue");
        scn.MoveLocationToTable(from); scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, ship); scn.BoardAsPilot(ship, rogue);
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.MOVE);
        int before = scn.GetLSForcePile().size();
        scn.LSMoveCard(ship, to); scn.PassAllResponses();
        assertEquals(to, ship.getAtLocation());
        assertEquals(before - expectedCost, scn.GetLSForcePile().size());
    }

    @Test public void actualXWingHyperspeedMoveCostsNoForce() { actualStarshipMove("xwing", 0); }
    @Test public void actualFalconHyperspeedMoveStillCostsOne() { actualStarshipMove("falcon", 1); }

    private void resolveTableLoss(String vehicle, int availableForce, boolean offersReplacement, boolean accept) {
        var scn = fixture();
        scn.StartGame();
        var location = scn.GetLSCard(vehicle.equals("speeder") || vehicle.isEmpty() ? "from" : "hoth");
        var rogue = scn.GetLSCard("rogue");
        scn.MoveLocationToTable(location);
        if (vehicle.isEmpty()) {
            scn.MoveCardsToLocation(location, rogue);
        } else {
            var ship = scn.GetLSCard(vehicle);
            scn.MoveCardsToLocation(location, ship);
            scn.BoardAsPilot(ship, rogue);
        }
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(availableForce);
        scn.DrainLSForcePileTo(availableForce);
        var forceBefore = List.copyOf(scn.GetLSForcePile());
        assertEquals(availableForce, forceBefore.size());

        var source = scn.GetDSStartingLocation();
        var loss = new TopLevelGameTextAction(source, scn.DS, source.getCardId());
        loss.appendEffect(new LoseCardFromTableEffect(loss, rogue));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, loss);
        if (scn.DSDecisionAvailable("optional response")) {
            scn.DSPass();
        }
        String replacement = "Place Veteran Rogue in Used Pile instead";
        assertEquals(offersReplacement, scn.LSCardActionAvailable(rogue, replacement));
        if (accept) {
            scn.LSUseCardAction(rogue, replacement);
        }
        scn.PassAllResponses();

        if (accept) {
            assertTrue(scn.GetLSUsedPile().contains(rogue));
            assertTrue(scn.GetLSUsedPile().contains(forceBefore.getFirst()));
            assertEquals(0, scn.GetLSForcePile().size());
        } else {
            assertTrue(scn.GetLSLostPile().contains(rogue));
            assertEquals(forceBefore, scn.GetLSForcePile());
        }
        if (!vehicle.isEmpty()) {
            assertEquals(location, scn.GetLSCard(vehicle).getAtLocation());
        }
    }

    @Test public void paysOneForceToReplaceActualXWingPilotLoss() { resolveTableLoss("xwing", 1, true, true); }
    @Test public void paysOneForceToReplaceActualT47PilotLoss() { resolveTableLoss("speeder", 1, true, true); }
    @Test public void mayDeclineActualLossReplacement() { resolveTableLoss("xwing", 1, true, false); }
    @Test public void cannotReplaceActualLossWithZeroForce() { resolveTableLoss("xwing", 0, false, false); }
    @Test public void cannotReplaceActualFalconPilotLoss() { resolveTableLoss("falcon", 1, false, false); }
    @Test public void cannotReplaceActualLossWhileOnFoot() { resolveTableLoss("", 1, false, false); }
}
