package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1263_019_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(),
                new HashMap<>(Map.of("tie", "1263_19", "factory", "1117_21", "pilot", "1_168",
                        "mech", "2_99", "other", "1_194", "system", "1_289")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        return scn;
    }

    @Test public void TieAvengerPrintedMetadataAndSeatsMatch() {
        var scn = scenario();
        var b = scn.GetDSCard("tie").getBlueprint();
        assertEquals("TIE Avenger", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon);
        assertEquals(4, b.getManeuver(), scn.epsilon);
        assertEquals(3, b.getHyperspeed(), scn.epsilon);
        assertEquals(2, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.STARSHIP, Icon.DAGOBAH, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6)));
        assertEquals(1, b.getPilotCapacity());
        assertEquals(1, scn.gameState().getAvailablePassengerCapacityForAstromech(scn.game().getModifiersQuerying(),
                scn.GetDSCard("tie"), null));
    }

    @Test public void TieAvengerFactoryAddsTwoForfeitOnlyToItsActualPilot() {
        for (boolean factoryOnTable : List.of(false, true)) {
            var scn = scenario();
            var tie = scn.GetDSCard("tie");
            var pilot = scn.GetDSCard("pilot");
            var other = scn.GetDSCard("other");
            scn.MoveCardsToLocation(scn.GetDSCard("system"), tie);
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), other);
            scn.BoardAsPilot(tie, pilot);
            int baseline = scn.GetForfeit(pilot);
            int otherForfeit = scn.GetForfeit(other);
            if (factoryOnTable) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("factory"));
            assertEquals(baseline + (factoryOnTable ? 2 : 0), scn.GetForfeit(pilot));
            assertEquals(otherForfeit, scn.GetForfeit(other));
        }
    }

    @Test public void TieAvengerActuallyDeploysAstromechAboardAndReshuffles() {
        var scn = scenario();
        var tie = scn.GetDSCard("tie");
        var mech = scn.GetDSCard("mech");
        var other = scn.GetDSCard("other");
        scn.MoveCardsToLocation(scn.GetDSCard("system"), tie);
        scn.BoardAsPilot(tie, scn.GetDSCard("pilot"));
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToTopOfDSReserveDeck(mech, other);
        scn.SkipToPhase(Phase.DEPLOY);
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        assertTrue(remaining.remove(mech));
        scn.gameState().getRandom().setSeed(4096);
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(tie, "Deploy an astromech aboard TIE Avenger");
        for (int i = 0; i < 45 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(mech)) {
                int index = scn.DSGetBPChoices().indexOf(other.getBlueprintId(true));
                assertTrue(index >= 0);
                assertEquals("false", scn.GetADParam(scn.DS, "selectable")[index]);
                scn.DSChooseCard(mech);
            } else if (scn.DSHasCardChoiceAvailable(tie)) scn.DSChooseCard(tie);
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected TIE Avenger deploy decision: " + scn.GetCurrentDecision().getText());
        }
        assertSame(tie, mech.getAttachedTo());
        assertEquals(force - (int) (float) mech.getBlueprint().getDeployCost(), scn.GetDSForcePileCount());
        assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
        assertNotEquals(remaining, scn.GetDSReserveDeck());
    }
}
