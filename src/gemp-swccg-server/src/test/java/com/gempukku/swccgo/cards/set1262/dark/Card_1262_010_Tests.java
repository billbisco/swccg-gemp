package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1262_010_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("capital", "1_140", "independent", "109_5", "fighter", "1_144", "pilot", "1_19")),
                new HashMap<>(Map.of("trap", "1262_10", "system", "1_289", "ownCapital", "1_301")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        return scn;
    }

    @Test public void ProgramTrapPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("trap").getBlueprint();
        assertEquals("Program Trap", b.getTitle());
        assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void ProgramTrapActuallyDeploysOnOpponentsCapitalOrIndependentStarshipAndReducesOnlyItsPower() {
        for (String type : List.of("capital", "independent")) {
            var scn = scenario();
            var trap = scn.GetDSCard("trap");
            var target = scn.GetLSCard(type);
            var other = scn.GetLSCard(type.equals("capital") ? "independent" : "capital");
            var fighter = scn.GetLSCard("fighter");
            var own = scn.GetDSCard("ownCapital");
            scn.MoveCardsToLocation(scn.GetDSCard("system"), target, other, fighter, own);
            scn.BoardAsPilot(scn.GetLSCard("capital"), scn.GetLSCard("pilot"));
            int power = scn.GetPower(target);
            int otherPower = scn.GetPower(other);
            scn.MoveCardsToDSHand(trap);
            scn.SkipToPhase(Phase.DEPLOY);
            scn.DSDeployCard(trap);
            assertTrue(scn.DSHasCardChoiceAvailable(target));
            assertTrue(scn.DSHasCardChoiceAvailable(other));
            assertFalse(scn.DSHasCardChoiceAvailable(fighter));
            assertFalse(scn.DSHasCardChoiceAvailable(own));
            scn.DSChooseCard(target);
            for (int i = 0; i < 35 && !scn.AwaitingDSDeployPhaseActions(); i++) {
                if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Program Trap deploy decision: " + scn.GetCurrentDecision().getText());
            }
            assertSame(target, trap.getAttachedTo());
            assertEquals(power - 2, scn.GetPower(target));
            assertEquals(otherPower, scn.GetPower(other));
        }
    }
}
