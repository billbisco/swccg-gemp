package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_070_Tests {
    private static final String UNSTACK = "Place a card from Crash Site Memorial in Used Pile";

    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("fixer", "1240_70"); put("camie", "7_9"); put("luke", "1_19");
            put("corvette", "1_140"); put("rogue", "3_66"); put("droid", "1_5");
            put("memorial", "1_45"); put("alter", "1_71"); put("control", "4_48"); put("planners", "1_60");
        }}, new HashMap<>() {{
            put("alter", "1_234"); put("vader", "1_168"); put("system", "1_282");
            put("dread", "106_13");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FixerPrintedAlienIdentityStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetLSCard("fixer").getBlueprint();
        assertEquals("Fixer", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.LIGHT, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(3, bp.getForfeit(), scn.epsilon);
        assertEquals(CardCategory.CHARACTER, bp.getCardCategory());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.ALIEN)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.ALIEN, Icon.PILOT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
    }

    private VirtualTableScenario Ready(boolean light, Phase phase) {
        var scn = GetScenario();
        scn.StartGame();
        if (light) scn.SkipToLSTurn(Phase.ACTIVATE);
        else scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.EnsureLSForcePile(10);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToLSHand(scn.GetLSCard("fixer"));
        scn.SkipToPhase(phase);
        return scn;
    }

    private void DeployFixer(VirtualTableScenario scn) {
        scn.LSDeployCard(scn.GetLSCard("fixer"));
        scn.LSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
    }

    @Test
    public void FixerActuallyDeploysForTwoWithoutCamieAtDestination() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.EnsureLSForcePile(2);
        DeployFixer(scn);
        assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(scn.GetDSStartingLocation(), scn.GetLSCard("fixer").getAtLocation());
    }

    @Test
    public void FixerCannotDeployForOneWithoutCamieAtDestination() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.EnsureLSForcePile(1);
        assertFalse(scn.LSDeployAvailable(scn.GetLSCard("fixer")));
        assertInZone(Zone.HAND, scn.GetLSCard("fixer"));
    }

    @Test
    public void FixerActuallyDeploysForOneToSameSiteAsCamie() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("camie"));
        scn.EnsureLSForcePile(1);
        DeployFixer(scn);
        assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(scn.GetDSStartingLocation(), scn.GetLSCard("fixer").getAtLocation());
        assertEquals(4, scn.GetForfeit(scn.GetLSCard("camie"))); // Original Camie recognizes the renamed Fixer.
    }

    @Test
    public void FixerDeploymentReductionIncludesCamieAboardEnclosedVehicleAtSameSite() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("rogue"));
        scn.BoardAsPilot(scn.GetLSCard("rogue"), scn.GetLSCard("luke"));
        scn.BoardAsPassenger(scn.GetLSCard("rogue"), scn.GetLSCard("camie"));
        scn.EnsureLSForcePile(1);
        DeployFixer(scn);
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void FixerDoesNotReceiveDestinationReductionWhenCamieIsElsewhere() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("camie"));
        scn.EnsureLSForcePile(2);
        DeployFixer(scn);
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void FixerAddsTwoPowerOnlyToStarshipHePilotsAndRemovalRestoresPower() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetLSCard("corvette"));
        assertEquals(5, scn.GetPower(scn.GetLSCard("corvette")));
        scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("fixer"));
        assertEquals(7, scn.GetPower(scn.GetLSCard("corvette")));
        scn.MoveCardsToLSHand(scn.GetLSCard("fixer"));
        assertEquals(5, scn.GetPower(scn.GetLSCard("corvette")));
    }

    @Test
    public void FixerAddsTwoPowerToVehicleHePilots() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("rogue"));
        scn.BoardAsPilot(scn.GetLSCard("rogue"), scn.GetLSCard("fixer"));
        assertEquals(5, scn.GetPower(scn.GetLSCard("rogue"))); // Printed3 + Fixer2.
    }

    @Test
    public void FixerDoesNotAddPilotPowerAsPassengerWithAnotherRealPilot() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("rogue"));
        scn.BoardAsPilot(scn.GetLSCard("rogue"), scn.GetLSCard("luke"));
        assertEquals(6, scn.GetPower(scn.GetLSCard("rogue")));
        scn.BoardAsPassenger(scn.GetLSCard("rogue"), scn.GetLSCard("fixer"));
        assertEquals(6, scn.GetPower(scn.GetLSCard("rogue")));
    }

    @Test
    public void FixerPilotBonusCreatesTwoActualBattleDamagePaidFromOpponentsLifeForce() {
        var scn = Ready(true, Phase.CONTROL);
        var system = scn.GetDSCard("system");
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, scn.GetLSCard("corvette"), scn.GetDSCard("dread"));
        scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("fixer"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.SkipToDamageSegment(false);
        assertEquals(2, scn.GetUnpaidDSBattleDamage()); // Corvette5+Fixer2 versus original Dreadnaught5.
        assertEquals(0, scn.GetUnpaidDSAttrition());
        int lost = scn.GetDSLostPileCount();
        int reserve = scn.GetDSReserveDeckCount();
        scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lost + 2, scn.GetDSLostPileCount());
        assertEquals(reserve - 2, scn.GetDSReserveDeckCount());
        assertTrue(scn.GetLSCard("corvette").getZone().isInPlay());
        assertTrue(scn.GetLSCard("fixer").getZone().isInPlay());
    }

    private VirtualTableScenario MemorialReady(boolean light) {
        var scn = Ready(light, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("fixer"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("memorial"));
        scn.StackCardsOn(scn.GetLSCard("memorial"), scn.GetLSCard("droid"), scn.GetLSCard("rogue"));
        return scn;
    }

    private void AwaitLSControl(VirtualTableScenario scn) {
        for (int i = 0; i < 12 && !scn.AwaitingLSControlPhaseActions(); i++) {
            assertEquals(Phase.CONTROL, scn.gameState().getCurrentPhase());
            assertNotNull(scn.GetCurrentDecision());
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    private void UseMemorialCard(VirtualTableScenario scn, String card) {
        AwaitLSControl(scn);
        int force = scn.GetLSForcePileCount();
        int reserve = scn.GetLSReserveDeckCount();
        int used = scn.GetLSUsedPileCount();
        scn.LSUseCardAction(scn.GetLSCard("fixer"), UNSTACK);
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard(card))) {
            scn.LSChooseCard(scn.GetLSCard(card));
        } else {
            // With one eligible card and a paid cost, native stacked-card selection is automatic.
            assertInZone(Zone.USED_PILE, scn.GetLSCard(card));
        }
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(reserve, scn.GetLSReserveDeckCount());
        assertEquals(used + 2, scn.GetLSUsedPileCount()); // One spent Force and the selected stacked card.
        assertInZone(Zone.USED_PILE, scn.GetLSCard(card));
        assertFalse(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard(card)));
    }

    @Test
    public void FixerUsesExactlyOneForceToMoveSelectedDroidFromMemorialToUsed() {
        var scn = MemorialReady(true);
        scn.EnsureLSForcePile(1);
        UseMemorialCard(scn, "droid");
        assertEquals(0, scn.GetLSForcePileCount());
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("rogue")));
    }

    @Test
    public void FixerMaySelectNonDroidCardAndDoesNotTakeItIntoHand() {
        var scn = MemorialReady(true);
        int hand = scn.GetLSHandCount();
        UseMemorialCard(scn, "rogue");
        assertEquals(hand, scn.GetLSHandCount());
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("droid")));
    }

    @Test
    public void FixerCannotMoveMemorialCardWithZeroActivatedForce() {
        var scn = MemorialReady(true);
        scn.DrainLSForcePileTo(0);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("fixer"), UNSTACK));
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("droid")));
    }

    @Test
    public void FixerMayMoveOnlyOneMemorialCardPerTurnEvenWhenAnotherRemains() {
        var scn = MemorialReady(true);
        UseMemorialCard(scn, "droid");
        AwaitLSControl(scn);
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("rogue")));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("fixer"), UNSTACK));
    }

    @Test
    public void FixerMayMoveAnotherMemorialCardDuringOpponentsNextTurn() {
        var scn = MemorialReady(true);
        UseMemorialCard(scn, "droid");
        scn.SkipToDSTurn(Phase.CONTROL);
        UseMemorialCard(scn, "rogue");
        assertInZone(Zone.USED_PILE, scn.GetLSCard("droid"));
    }

    @Test
    public void FixerDoesNotOfferMemorialActionWhenItIsAbsentOrEmpty() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("fixer"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("fixer"), UNSTACK));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("memorial"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("fixer"), UNSTACK));
    }

    @Test
    public void FixerMayDeclineMemorialActionLeavingBothCardsStacked() {
        var scn = MemorialReady(true);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("droid")));
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("rogue")));
    }

    @Test
    public void FixerMayUseMemorialActionDuringMovePhase() {
        var scn = MemorialReady(true);
        scn.SkipToPhase(Phase.MOVE);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("fixer"), UNSTACK);
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSCard("droid"));
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetLSCard("droid"));
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    private void PlayAlter(VirtualTableScenario scn, boolean light, String effect) {
        if (light) {
            scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
            scn.PrepareLSDestiny(0);
            scn.LSPlayCard(scn.GetLSCard("alter"), "Draw destiny to cancel");
            if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(effect))) scn.LSChooseCard(scn.GetLSCard(effect));
            if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.LSChooseCard(scn.GetLSCard("luke"));
        } else {
            scn.MoveCardsToDSHand(scn.GetDSCard("alter"));
            scn.PrepareDSDestiny(0);
            scn.DSPlayCard(scn.GetDSCard("alter"), "Draw destiny to cancel");
            if (scn.DSHasCardChoiceAvailable(scn.GetLSCard(effect))) scn.DSChooseCard(scn.GetLSCard(effect));
            if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("vader"))) scn.DSChooseCard(scn.GetDSCard("vader"));
        }
    }

    private void ResolvePileOrder(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            scn.PassAllResponses();
            boolean chose = false;
            if (scn.LSAnyDecisionsAvailable()) {
                for (String name : List.of("droid", "rogue", "memorial")) {
                    if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(name))) {
                        assertTrue(scn.LSGetDecision().getText(), scn.LSGetDecision().getText().toLowerCase().contains("pile"));
                        scn.LSChooseCard(scn.GetLSCard(name));
                        chose = true;
                        break;
                    }
                }
            }
            if (!chose) return;
        }
        fail("Pile ordering did not finish");
    }

    @Test
    public void FixerSavesAllMemorialCardsInUsedWhenOpponentActuallyCancelsIt() {
        var scn = MemorialReady(false);
        scn.DrainLSForcePileTo(0);
        PlayAlter(scn, false, "memorial");
        ResolvePileOrder(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("memorial"));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("droid"));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("rogue"));
        assertEquals(0, scn.GetLSForcePileCount()); // Mandatory cleanup has no fee.
        assertTrue(scn.GetLSCard("fixer").getZone().isInPlay());
    }

    @Test
    public void FixerDoesNotSaveMemorialCardsWhenYourOwnCardCancelsIt() {
        // Mandatory regression for integration182 request6701550090: source owner, not performing player.
        var scn = MemorialReady(true);
        PlayAlter(scn, true, "memorial");
        ResolvePileOrder(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("memorial"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("droid"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("rogue"));
    }

    @Test
    public void FixerMustBeActiveToSaveMemorialCardsFromOpponentCancellation() {
        var scn = MemorialReady(false);
        scn.MoveCardsToLSHand(scn.GetLSCard("fixer"));
        PlayAlter(scn, false, "memorial");
        ResolvePileOrder(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("droid"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("rogue"));
    }

    @Test
    public void FixerDoesNotCreateAnEmptyCleanupChoiceWhenMemorialHasNoCards() {
        var scn = MemorialReady(false);
        scn.MoveCardsToLSHand(scn.GetLSCard("droid"), scn.GetLSCard("rogue"));
        int used = scn.GetLSUsedPileCount();
        PlayAlter(scn, false, "memorial");
        ResolvePileOrder(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("memorial"));
        assertEquals(used, scn.GetLSUsedPileCount());
        assertInZone(Zone.HAND, scn.GetLSCard("droid"));
        assertInZone(Zone.HAND, scn.GetLSCard("rogue"));
    }

    @Test
    public void FixerDoesNotUnstackMemorialCardsWhenADifferentEffectLeavesTable() {
        var scn = MemorialReady(false);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("planners"));
        PlayAlter(scn, false, "planners");
        ResolvePileOrder(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("planners"));
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("droid")));
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("rogue")));
    }

    @Test
    public void FixerDoesNotMoveCardsIfOpponentAlterIsActuallyCanceledBeforeItsResult() {
        var scn = MemorialReady(false);
        scn.MoveCardsToLSHand(scn.GetLSCard("control"));
        PlayAlter(scn, false, "memorial");
        for (int i = 0; i < 40; i++) {
            if (scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(scn.GetLSCard("control"))) break;
            assertNotNull(scn.GetCurrentDecision());
            assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("control")));
        scn.LSPlayCard(scn.GetLSCard("control"));
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("alter"));
        assertTrue(scn.GetLSCard("memorial").getZone().isInPlay());
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("droid")));
        assertTrue(scn.IsStackedOn(scn.GetLSCard("memorial"), scn.GetLSCard("rogue")));
    }
}
