package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_057_Tests {
    @Test
    public void finalTIEPrintingUsesTheNamedUniquePermanentPilotAndNativeAttritionImmunity() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>(Map.of("black", "1261_57")),
                14, 14, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var ship = scn.GetDSCard("black");
        var bp = ship.getBlueprint();
        assertEquals("DS-61-11 In Black 11", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(4, bp.getPower(), scn.epsilon);
        assertEquals(3, bp.getManeuver(), scn.epsilon);
        assertNull(bp.getHyperspeed());
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.STARFIGHTER, bp.getCardSubtype());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.STARSHIP)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.STARSHIP, Icon.PILOT, Icon.DEATH_STAR_II,
                Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON)));
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ship);
        var pilots = bp.getPermanentsAboard(ship);
        assertEquals(1, pilots.size());
        var pilot = pilots.getFirst();
        assertTrue(pilot.isPilot());
        assertEquals("DS-61-11", pilot.getTitle(scn.game()));
        assertEquals(Uniqueness.UNIQUE, pilot.getUniqueness());
        assertEquals(2, pilot.getAbility(), scn.epsilon);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship),
                scn.epsilon);
    }

    private VirtualTableScenario sienarScenario() {
        return new VirtualTableScenario(new HashMap<>(),
                new HashMap<>(Map.of("black", "1261_57", "sienar", "7_241", "tie", "1_304", "black2", "1_299", "pilot", "1_180",
                        "filler1", "1_194", "filler2", "1_194", "filler3", "1_194", "filler4", "1_194")),
                14, 14, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    /** Deploys a TIE (Black 2 with the Imperial Pilot, or the permanently piloted TIE Fighter alone) to the DS system (with or without the Imperial Pilot) and returns how many cards Sienar
     * Fleet Systems retrieved. */
    private int sienarRetrievalForTieDeploy(boolean blackElevenOnTable, boolean withPilot) {
        var scn = sienarScenario();
        var tie = scn.GetDSCard(withPilot ? "black2" : "tie");
        var pilot = scn.GetDSCard("pilot");
        scn.StartGame();
        var system = scn.GetDSStartingLocation();
        scn.AttachCardsTo(system, scn.GetDSCard("sienar"));
        if (blackElevenOnTable)
            scn.MoveCardsToLocation(system, scn.GetDSCard("black"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("filler1"), scn.GetDSCard("filler2"),
                scn.GetDSCard("filler3"), scn.GetDSCard("filler4"));
        scn.MoveCardsToDSHand(tie, pilot);
        scn.DSActivateForceCheat(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int lostBefore = scn.GetDSLostPileCount();
        assertTrue(scn.DSDeployAvailable(tie));
        scn.DSDeployCard(tie);
        for (int i = 0; i < 40; i++) {
            if (tie.getZone() == Zone.AT_LOCATION && scn.AwaitingDSDeployPhaseActions())
                break;
            var decision = scn.GetCurrentDecision();
            String text = decision.getText().toLowerCase();
            boolean ds = scn.DSGetDecision() != null;
            if (ds && text.startsWith("do you want to simultaneously deploy")) {
                if (withPilot) scn.DSChooseYes(); else scn.DSChooseNo();
            } else if (ds && withPilot && scn.DSHasCardChoiceAvailable(pilot)) {
                scn.DSChooseCard(pilot);
            } else if (ds && scn.DSHasCardChoiceAvailable(system)) {
                scn.DSChooseCard(system);
            } else if (ds && text.contains("capacity slot")) {
                scn.ChooseOption(scn.DS, scn.DSGetMultipleChoices().getFirst());
            } else if (ds) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
        }
        assertEquals(Zone.AT_LOCATION, tie.getZone());
        assertEquals("pilot zone " + pilot.getZone() + "; log " + String.join(" | ", scn.gameState().getLastMessages()),
                withPilot, pilot.getZone() != Zone.HAND);
        return lostBefore - scn.GetDSLostPileCount();
    }

    @Test(timeout = 15000)
    public void sienarRetrievalIsPlusOneForTieDeployedSimultaneouslyWithPilot() {
        assertEquals(2, sienarRetrievalForTieDeploy(true, true));
    }

    @Test(timeout = 15000)
    public void noBonusForTieDeployedWithoutPilot() {
        assertEquals(1, sienarRetrievalForTieDeploy(true, false));
    }

    @Test(timeout = 15000)
    public void noBonusWithoutBlackEleven() {
        assertEquals(1, sienarRetrievalForTieDeploy(false, true));
    }
}
