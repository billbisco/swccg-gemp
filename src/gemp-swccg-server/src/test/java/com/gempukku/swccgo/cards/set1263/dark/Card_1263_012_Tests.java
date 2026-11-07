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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_012_Tests {
    private static final String IMMEDIATE = "Take Block 6 Grotto Werribee or an Immediate Effect";
    private static final String FREE = "Take an Effect that deploys for free";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(),
                new HashMap<>(Map.of("plan", "1263_12", "immediate", "1112_26", "free", "9_134",
                        "paid", "9_127", "trooper", "1_194", "grotto", "1263_4", "decipherGrotto", "12_107", "unexpected", "4_152")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void OperationalAsPlannedPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("plan").getBlueprint();
        assertEquals("Operational As Planned", b.getTitle());
        assertEquals("\"We shall double our efforts.\"", b.getLore());
        assertEquals(5, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.USED, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void OperationalAsPlannedOffersOnlyAffordableModes() {
        for (int force : List.of(0, 1, 2)) {
            var scn = scenario();
            var plan = scn.GetDSCard("plan");
            scn.MoveCardsToDSHand(plan);
            scn.SkipToPhase(Phase.CONTROL);
            setForce(scn, force);
            scn.SkipToPhase(Phase.DEPLOY);
            assertEquals(force >= 1, scn.DSCardPlayAvailable(plan, IMMEDIATE));
            assertEquals(force >= 2, scn.DSCardPlayAvailable(plan, FREE));
        }
    }

    @Test public void OperationalAsPlannedActuallyTakesBlockSixGrottoImmediateOrFreeEffectAndReshuffles() {
        for (String selection : List.of("grotto", "immediate", "free")) {
            int cost = selection.equals("free") ? 2 : 1;
            var scn = scenario();
            var plan = scn.GetDSCard("plan");
            var immediate = scn.GetDSCard("immediate");
            var free = scn.GetDSCard("free");
            var paid = scn.GetDSCard("paid");
            var grotto = scn.GetDSCard("grotto");
            var decipher = scn.GetDSCard("decipherGrotto");
            var target = scn.GetDSCard(selection);
            var wrong = cost == 1 ? free : immediate;
            scn.MoveCardsToDSHand(plan);
            scn.SkipToPhase(Phase.CONTROL);
            scn.MoveCardsToTopOfDSReserveDeck(immediate, free, paid, grotto, decipher, scn.GetDSCard("trooper"));
            setForce(scn, cost);
            scn.SkipToPhase(Phase.DEPLOY);
            var remaining = new ArrayList<>(scn.GetDSReserveDeck());
            remaining.remove(target);
            scn.gameState().getRandom().setSeed(4096);
            assertEquals(cost, scn.GetDSForcePileCount());
            scn.DSPlayCard(plan, cost == 1 ? IMMEDIATE : FREE);
            boolean selected = false;
            for (int i = 0; i < 45 && !scn.AwaitingDSDeployPhaseActions(); i++) {
                if (scn.DSHasCardChoiceAvailable(target)) {
                    var choices = scn.DSGetBPChoices();
                    var selectable = scn.GetADParam(scn.DS, "selectable");
                    int targetIndex = choices.indexOf(target.getBlueprintId(true));
                    int wrongIndex = choices.indexOf(wrong.getBlueprintId(true));
                    int paidIndex = choices.indexOf(paid.getBlueprintId(true));
                    int decipherIndex = choices.indexOf(decipher.getBlueprintId(true));
                    assertTrue(targetIndex >= 0 && wrongIndex >= 0 && paidIndex >= 0 && decipherIndex >= 0);
                    assertEquals("The same-title Decipher printing is not Block 6", "false", selectable[decipherIndex]);
                    assertEquals("true", selectable[targetIndex]);
                    assertEquals("false", selectable[wrongIndex]);
                    assertEquals("false", selectable[paidIndex]);
                    scn.DSChooseCard(target);
                    selected = true;
                } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Operational As Planned search decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(selected);
            assertInZone(Zone.HAND, target);
            assertInZone(Zone.USED_PILE, plan);
            assertEquals(0, scn.GetDSForcePileCount());
            assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
            assertNotEquals(remaining, scn.GetDSReserveDeck());
        }
    }
    @Test public void OperationalAsPlannedStaysInHandWhenRetrievedByARealUnrelatedSearch() {
        var scn = scenario();
        var plan = scn.GetDSCard("plan");
        var unexpected = scn.GetDSCard("unexpected");
        scn.MoveCardsToDSHand(unexpected);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(plan);
        setForce(scn, 2);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(unexpected, "Take Interrupt into hand from Reserve Deck");
        boolean selected = false;
        for (int i = 0; i < 40 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (!selected && scn.DSHasCardChoiceAvailable(plan)) {
                scn.DSChooseCard(plan);
                selected = true;
            } else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) {
                assertFalse(scn.DSCardActionAvailable(plan, "Reveal Operational As Planned"));
                scn.DSPass();
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else fail("Unexpected unrelated search decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(selected);
        assertInZone(Zone.HAND, plan);
        assertInZone(Zone.LOST_PILE, unexpected);
    }

    private void setForce(VirtualTableScenario scn, int amount) {
        scn.EnsureDSForcePile(amount);
        while (scn.GetDSForcePileCount() > amount) {
            scn.MoveCardsToTopOfDSReserveDeck((com.gempukku.swccgo.game.PhysicalCardImpl)
                    scn.gameState().getForcePile(scn.DS).getFirst());
        }
    }
}
