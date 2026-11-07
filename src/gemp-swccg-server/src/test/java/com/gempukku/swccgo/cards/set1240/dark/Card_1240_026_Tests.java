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

public class Card_1240_026_Tests {
    private VirtualTableScenario GetScenario() {
        StartingSetup shields = new StartingSetup() {
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("s1", "13_81"); put("s2", "13_54");
                    put("s3", "13_84"); put("s4", "13_51");
                }};
            }
            public void Setup(VirtualTableScenario scn) { }
        };
        StartingSetup lightShields = new StartingSetup() {
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{ put("lsShield", "13_1"); }};
            }
            public void Setup(VirtualTableScenario scn) { }
        };
        return new VirtualTableScenario(new HashMap<>() {{
            put("lando", "13_27"); put("chewie", "13_9"); put("luke", "1_19");
            put("trooper", "1_28"); put("corvette", "1_140"); put("cantina", "1_128");
            put("honor", "9_33"); put("control", "4_48"); put("sacrifice", "1104_9");
            put("fear", "13_5");
            put("leia", "13_29");
        }}, new HashMap<>() {{
            put("he", "1240_26"); put("he2", "1240_26"); put("fear", "13_69");
            put("trooper", "1_194"); put("vader", "1_168"); put("dantooine", "1_282");
            put("deathstar", "2_143"); put("lost", "1_172");
            put("garrison", "13_87"); put("bombing", "4_117"); put("advisor", "6_160");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt, StartingSetup.DeclineDSStartingInterrupt,
                lightShields, shields, VirtualTableScenario.Open);
    }

    @Test
    public void HeIsNotReadyAndImperialPropagandaPrintedIdentityIconsAndBothTitlesAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("he").getBlueprint();
        assertEquals("He Is Not Ready & Imperial Propaganda", bp.getTitle());
        assertTrue(bp.isComboCard());
        assertEquals(List.of(Title.He_Is_Not_Ready, Title.Imperial_Propaganda), bp.getTitles());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(5, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
        assertTrue(bp.isImmuneToCardTitle(Title.Control));
        assertTrue(bp.isImmuneToCardTitle(Title.Honor_Of_The_Jedi));
    }

    private VirtualTableScenario Ready() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToDSHand(scn.GetDSCard("he"), scn.GetDSCard("he2"));
        scn.MoveCardsToLSHand(scn.GetLSCard("lando"), scn.GetLSCard("chewie"), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        scn.EnsureLSForcePile(12);
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void Reach(VirtualTableScenario scn, String action) {
        for (int i = 0; i < 40; i++) {
            if (scn.DSAnyActionsAvailable() && scn.DSCardActionAvailable(scn.GetDSCard("he"), action)) return;
            assertNotNull(scn.GetCurrentDecision());
            assertTrue(scn.GetCurrentDecision().getText(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Expected He Is Not Ready action: " + action);
    }

    private void DeployHost(VirtualTableScenario scn, String name) {
        var host = scn.GetLSCard(name);
        scn.LSDeployCard(host);
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassForceUseResponses();
        scn.PassCardPlayResponses();
    }

    private void Attach(VirtualTableScenario scn, String host) {
        DeployHost(scn, host);
        Reach(scn, "Deploy");
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(scn.GetDSCard("he"));
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard(host))) scn.DSChooseCard(scn.GetLSCard(host));
        passOptional(scn);
        assertEquals(scn.GetLSCard(host), scn.GetDSCard("he").getAttachedTo());
        assertEquals(force, scn.GetDSForcePileCount());
    }

    private void PayInitialLoss(VirtualTableScenario scn) {
        assertTrue(scn.AwaitingLSForceLossPayment());
        scn.LSPayForceLossFromReserveDeck();
        passOptional(scn);
    }

    private void PassWithoutOffer(VirtualTableScenario scn, String action) {
        for (int i = 0; i < 40; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) return;
            if (scn.DSAnyActionsAvailable()) assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("he"), action));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Optional response windows did not finish");
    }

    @Test
    public void HeIsNotReadyActuallyDeploysForFreeAndMakesOpponentLoseOneForce() {
        var scn = Ready();
        int lost = scn.GetLSLostPileCount();
        int reserve = scn.GetLSReserveDeckCount();
        Attach(scn, "lando");
        PayInitialLoss(scn);
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        assertEquals(reserve - 1, scn.GetLSReserveDeckCount());
        assertEquals(3, scn.GetPower(scn.GetLSCard("lando")));
    }

    @Test
    public void HeIsNotReadyInitialForceLossIsImmuneToHonorOfTheJedi() {
        var scn = Ready();
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("honor"));
        Attach(scn, "lando");
        int lost = scn.GetLSLostPileCount();
        PayInitialLoss(scn);
        assertEquals(lost + 1, scn.GetLSLostPileCount());
    }

    @Test
    public void HeIsNotReadyMayRetrieveOneForceAfterAttachment() {
        var scn = Ready();
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("lost"));
        Attach(scn, "lando");
        // Required initial loss resolves before the optional retrieval window.
        if (scn.AwaitingLSForceLossPayment()) scn.LSPayForceLossFromReserveDeck();
        Reach(scn, "Retrieve");
        int reserve = scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(scn.GetDSCard("he"), "Retrieve");
        passOptional(scn);
        assertInZone(Zone.USED_PILE, scn.GetDSCard("lost"));
        assertEquals(reserve, scn.GetDSReserveDeckCount());
    }

    @Test
    public void HeIsNotReadyMayDeclineRetrievalAndLeaveLostPileUnchanged() {
        var scn = Ready();
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("lost"));
        Attach(scn, "lando");
        PayInitialLoss(scn);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("lost"));
    }

    @Test
    public void HeIsNotReadyCannotDeployOnJustDeployedCardWithoutMaintenanceIcon() {
        var scn = Ready();
        DeployHost(scn, "luke");
        PassWithoutOffer(scn, "Deploy");
        assertInZone(Zone.HAND, scn.GetDSCard("he"));
    }

    @Test
    public void HeIsNotReadyRequiresMaintenanceEvenWhenNewCardHasReflectionsThreeIcon() {
        var scn = Ready();
        DeployHost(scn, "leia");
        PassWithoutOffer(scn, "Deploy");
        assertInZone(Zone.HAND, scn.GetDSCard("he"));
    }

    @Test
    public void HeIsNotReadyCannotDeployNormallyOnPreviouslyDeployedMaintenanceCard() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("lando"));
        if (scn.DSAnyActionsAvailable()) assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("he")));
        scn.SkipToPhase(Phase.BATTLE);
        if (scn.GetADParam(scn.DS, "actionText") != null) assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("he")));
    }

    @Test
    public void HeIsNotReadyTargetLosesThreePowerButKeepsItsPrintedAbility() {
        var scn = Ready();
        Attach(scn, "lando");
        PayInitialLoss(scn);
        assertEquals(3, scn.GetPower(scn.GetLSCard("lando")));
        assertEquals(3, scn.GetAbility(scn.GetLSCard("lando")));
        scn.MoveCardsToDSHand(scn.GetDSCard("he"));
        assertEquals(6, scn.GetPower(scn.GetLSCard("lando")));
        assertEquals(3, scn.GetAbility(scn.GetLSCard("lando")));
    }

    @Test
    public void HeIsNotReadyPreventsTargetAbilityProvidingNormalBattleDestinyButNotAddedDestiny() {
        var scn = Ready();
        Attach(scn, "lando");
        PayInitialLoss(scn);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("trooper"), scn.GetDSCard("vader"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareLSDestiny(2);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.SkipToDamageSegment(true);
        // Lando(3) + Rebel Trooper(1) normally reaches ability4; only Lando's added draw remains.
        assertEquals(1, scn.GetLSBattleDestinyCount());
        assertEquals(2, scn.GetLSTotalDestiny());
    }


    private VirtualTableScenario MaintenanceReady() {
        var scn = Ready();
        Attach(scn, "lando");
        PayInitialLoss(scn);
        scn.SkipToPhase(Phase.DRAW);
        passOptional(scn);
        assertTrue(scn.LSDecisionAvailable("Choose extra maintenance option"));
        return scn;
    }

    @Test
    public void HeIsNotReadyDrawPhaseMaintenanceMayLoseOneForceAndKeepTarget() {
        var scn = MaintenanceReady();
        int lost = scn.GetLSLostPileCount();
        scn.LSChooseOption("Lose 1 Force");
        passOptional(scn);
        scn.LSPayForceLossFromReserveDeck();
        passOptional(scn);
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        assertEquals(scn.GetLSCard("lando"), scn.GetDSCard("he").getAttachedTo());
    }

    @Test
    public void HeIsNotReadyDrawPhaseMaintenanceMayLoseTargetAndEffectReturnsToUsed() {
        var scn = MaintenanceReady();
        scn.LSChooseOption("Place target in Lost Pile");
        passOptional(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("lando"));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("he"));
        assertFalse(scn.GetLSCard("lando").getZone() == Zone.OUT_OF_PLAY);
    }

    @Test
    public void HeIsNotReadyAdditionalDrawPhaseCostDoesNotReplacePrintedEndTurnMaintenance() {
        var scn = MaintenanceReady();
        scn.LSChooseOption("Lose 1 Force");
        passOptional(scn);
        scn.LSPayForceLossFromReserveDeck();
        passOptional(scn);
        scn.SkipToPhase(Phase.END_OF_TURN);
        passOptional(scn);
        assertTrue(scn.LSDecisionAvailable("maintenance"));
        assertEquals(scn.GetLSCard("lando"), scn.GetDSCard("he").getAttachedTo());
    }

    private VirtualTableScenario DrainReady(int amount) {
        var scn = Ready();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        var location = amount == 3 ? scn.GetDSCard("deathstar")
                : amount == 2 ? scn.GetLSCard("cantina") : scn.GetDSCard("dantooine");
        scn.MoveLocationToTable(location);
        scn.MoveCardsToLocation(location, amount == 2 ? scn.GetLSCard("luke") : scn.GetLSCard("corvette"));
        scn.SkipToPhase(Phase.CONTROL);
        scn.LSForceDrainAt(location);
        return scn;
    }

    private void ReduceDrain(int amount) {
        var scn = DrainReady(amount);
        Reach(scn, "Reduce Force loss");
        int lost = scn.GetDSLostPileCount();
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(scn.GetDSCard("he"));
        passOptional(scn);
        if (scn.AwaitingDSForceLossPayment()) scn.DSPayRemainingForceLossFromReserveDeck();
        passOptional(scn);
        assertEquals(lost + Math.max(0, amount - 2), scn.GetDSLostPileCount());
        assertEquals(force, scn.GetDSForcePileCount());
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("he"));
        assertNull(scn.GetDSCard("he").getAttachedTo());
    }

    @Test public void HeIsNotReadyAndImperialPropagandaReducesActualForceDrainThreeToOne() { ReduceDrain(3); }
    @Test public void HeIsNotReadyAndImperialPropagandaReducesActualForceDrainTwoToZero() { ReduceDrain(2); }
    @Test public void HeIsNotReadyAndImperialPropagandaReducesActualForceDrainOneToZeroWithoutNegativeLoss() { ReduceDrain(1); }


    @Test
    public void HeIsNotReadyAndImperialPropagandaOnTableDoesNotReduceLaterForceLoss() {
        var scn = DrainReady(3);
        Reach(scn, "Reduce Force loss");
        scn.DSPlayCard(scn.GetDSCard("he"));
        passOptional(scn);
        scn.DSPayRemainingForceLossFromReserveDeck();
        passOptional(scn);
        scn.MoveCardsToDSHand(scn.GetDSCard("he2"));
        scn.MoveCardsToBottomOfDSReserveDeck(scn.GetDSCard("he2"));
        scn.SkipToLSTurn(Phase.CONTROL);
        int lost = scn.GetDSLostPileCount();
        scn.LSForceDrainAt(scn.GetDSCard("deathstar"));
        passOptional(scn);
        scn.DSPayRemainingForceLossFromReserveDeck();
        passOptional(scn);
        assertEquals(lost + 3, scn.GetDSLostPileCount());
    }

    @Test
    public void HeIsNotReadyAndImperialPropagandaTableModeDoesNotRetrieveForceOrMakeOpponentLoseForce() {
        var scn = DrainReady(2);
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("lost"));
        int lost = scn.GetLSLostPileCount();
        Reach(scn, "Reduce Force loss");
        scn.DSPlayCard(scn.GetDSCard("he"));
        passOptional(scn);
        assertEquals(lost, scn.GetLSLostPileCount());
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("lost"));
        assertFalse(scn.DSAnyActionsAvailable() && scn.DSCardActionAvailable(scn.GetDSCard("he"), "Retrieve"));
    }

    private void SetupShields(VirtualTableScenario scn) {
        var fear = scn.GetDSCard("fear");
        scn.MoveCardsToDSSideOfTable(fear);
        scn.StackCardsOn(fear, scn.GetDSCard("s1"), scn.GetDSCard("s2"), scn.GetDSCard("s3"), scn.GetDSCard("s4"));
    }

    @Test
    public void HeIsNotReadyMayPlayShieldFromOwnStartingEffectAfterAttachment() {
        var scn = Ready();
        SetupShields(scn);
        Attach(scn, "lando");
        scn.LSPayForceLossFromReserveDeck();
        Reach(scn, "Play a Defensive Shield");
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(scn.GetDSCard("he"), "Play a Defensive Shield");
        scn.DSChooseCard(scn.GetDSCard("s4"));
        passOptional(scn);
        assertTrue(scn.GetDSCard("s4").getZone().isInPlay());
        assertEquals(force, scn.GetDSForcePileCount());
        assertFalse(scn.IsStackedOn(scn.GetDSCard("fear"), scn.GetDSCard("s4")));
    }

    @Test
    public void HeIsNotReadyMayDeclineShieldAndLeaveItUnderStartingEffect() {
        var scn = Ready();
        SetupShields(scn);
        Attach(scn, "lando");
        PayInitialLoss(scn);
        assertTrue(scn.IsStackedOn(scn.GetDSCard("fear"), scn.GetDSCard("s4")));
    }



    @Test
    public void HeIsNotReadyAndImperialPropagandaTableModeDoesNotOfferShieldPermission() {
        var scn = DrainReady(2);
        SetupShields(scn);
        Reach(scn, "Reduce Force loss");
        scn.DSPlayCard(scn.GetDSCard("he"));
        PassWithoutOffer(scn, "Play a Defensive Shield");
        passOptional(scn);
        assertTrue(scn.IsStackedOn(scn.GetDSCard("fear"), scn.GetDSCard("s4")));
    }

    @Test
    public void HeIsNotReadyCannotDeployOnOwnJustDeployedMaintenanceCard() {
        var scn = Ready();
        scn.MoveCardsToDSHand(scn.GetDSCard("garrison"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        scn.DSDeployCard(scn.GetDSCard("garrison"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassForceUseResponses();
        scn.PassCardPlayResponses();
        PassWithoutOffer(scn, "Deploy");
        assertInZone(Zone.HAND, scn.GetDSCard("he"));
    }

    @Test
    public void HeIsNotReadyAndImperialPropagandaCannotReduceActualForceLossDuringOwnDeployPhase() {
        var scn = Ready();
        // Secondary real OVS producer: the original maintenance counter is the matching opposite-side effect.
        scn.MoveCardsToLSHand(scn.GetLSCard("sacrifice"));
        scn.MoveCardsToDSHand(scn.GetDSCard("garrison"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        scn.DSDeployCard(scn.GetDSCard("garrison"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassForceUseResponses();
        scn.PassCardPlayResponses();
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("sacrifice")));
        scn.LSPlayCard(scn.GetLSCard("sacrifice"));
        if (scn.LSHasCardChoiceAvailable(scn.GetDSCard("garrison"))) scn.LSChooseCard(scn.GetDSCard("garrison"));
        PassWithoutOffer(scn, "Reduce Force loss");
        passOptional(scn);
        assertEquals(scn.GetDSCard("garrison"), scn.GetLSCard("sacrifice").getAttachedTo());
        int lost = scn.GetDSLostPileCount();
        assertTrue(scn.AwaitingDSForceLossPayment());
        scn.DSPayForceLossFromReserveDeck();
        passOptional(scn);
        assertEquals(lost + 1, scn.GetDSLostPileCount());
        assertInZone(Zone.HAND, scn.GetDSCard("he"));
    }


    @Test
    public void HeIsNotReadyLostAsUnitOfForceDoesNotUseItsTableOnlyReturnText() {
        var scn = DrainReady(1);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("he"));
        scn.MoveCardsToBottomOfDSReserveDeck(scn.GetDSCard("he2"));
        passOptional(scn);
        scn.DSPayForceLossFromReserveDeck();
        passOptional(scn);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("he"));
    }

    @Test
    public void HeIsNotReadyExtraMaintenanceLosesLifeForceWithoutRequiringActivatedForce() {
        var scn = MaintenanceReady();
        scn.DrainLSForcePileTo(0);
        int lost = scn.GetLSLostPileCount();
        scn.LSChooseOption("Lose 1 Force");
        passOptional(scn);
        scn.LSPayForceLossFromReserveDeck();
        passOptional(scn);
        assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        assertEquals(scn.GetLSCard("lando"), scn.GetDSCard("he").getAttachedTo());
    }

    @Test
    public void HeIsNotReadyWithEmptyLostPileAndNoStartingEffectOffersNeitherOptionalReward() {
        var scn = Ready();
        assertEquals(0, scn.GetDSLostPileCount());
        Attach(scn, "lando");
        scn.LSPayForceLossFromReserveDeck();
        PassWithoutOffer(scn, "Retrieve");
        PassWithoutOffer(scn, "Play a Defensive Shield");
        assertEquals(0, scn.GetDSLostPileCount());
    }

    @Test
    public void HeIsNotReadyAndImperialPropagandaCannotReduceUsingForceDuringOpponentsControl() {
        var scn = Ready();
        scn.MoveCardsToDSHand(scn.GetDSCard("advisor"));
        scn.MoveCardsToBottomOfDSReserveDeck(scn.GetDSCard("he2"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(3);
        scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("advisor")));
        int lost = scn.GetDSLostPileCount();
        scn.DSPlayCard(scn.GetDSCard("advisor"));
        PassWithoutOffer(scn, "Reduce Force loss");
        passOptional(scn);
        scn.DSChooseCard(scn.GetDSCard("he2"));
        passOptional(scn);
        assertEquals(lost, scn.GetDSLostPileCount());
        assertInZone(Zone.HAND, scn.GetDSCard("he"));
        assertInZone(Zone.HAND, scn.GetDSCard("he2"));
    }
    @Test
    public void HeIsNotReadyInitialLossStopsAtExactlyOneAfterAllForceLossIsPaid() {
        var scn = Ready();
        int lost = scn.GetLSLostPileCount();
        Attach(scn, "lando");
        scn.LSPayRemainingForceLossFromReserveDeck();
        passOptional(scn);
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        assertFalse(scn.AwaitingLSForceLossPayment());
    }

    @Test
    public void HeIsNotReadyExtraMaintenanceStopsAtExactlyOneAfterAllLossIsPaid() {
        var scn = MaintenanceReady();
        scn.DrainLSForcePileTo(0);
        int lost = scn.GetLSLostPileCount();
        scn.LSChooseOption("Lose 1 Force");
        passOptional(scn);
        scn.LSPayRemainingForceLossFromReserveDeck();
        passOptional(scn);
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        assertEquals(0, scn.GetLSForcePileCount());
        assertFalse(scn.AwaitingLSForceLossPayment());
    }

    @Test
    public void HeIsNotReadyRetrievesOnlyOneWhenTwoLostCardsAreAvailable() {
        var scn = Ready();
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("trooper"), scn.GetDSCard("lost"));
        Attach(scn, "lando");
        scn.LSPayForceLossFromReserveDeck();
        Reach(scn, "Retrieve");
        int reserve = scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(scn.GetDSCard("he"), "Retrieve");
        passOptional(scn);
        assertInZone(Zone.USED_PILE, scn.GetDSCard("lost"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("trooper"));
        assertEquals(1, scn.GetDSLostPileCount());
        assertEquals(reserve, scn.GetDSReserveDeckCount());
    }

    /** Passes every pending optional-response window (PassAllResponses stops after 20), then any remaining responses. */
    private static void passOptional(VirtualTableScenario scn) {
        for (int i = 0; i < 100; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }
}
