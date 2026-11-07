package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_044_Tests {
    private static final String UPLOAD = "Take an Episode I Interrupt into hand";
    private static final String STACK = "Stack a card on Credits Will Do Fine";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("qui", "1250_44"); put("chamber", "12_76"); put("naboo", "12_83");
            put("credits", "12_42"); put("episode", "12_68"); put("control", "12_57");
            put("oldInterrupt", "1_109"); put("token", "1_17"); put("otherToken", "1_28"); put("luke", "1_19");
            put("quiWeapon", "11_49"); put("lukeWeapon", "9_90");
        }}, new HashMap<>() {{
            put("search", "8_131"); put("vader", "1_168"); put("trooper", "1_194");
            put("vaderWeapon", "1_324"); put("disarmed", "1_214"); put("beaten", "5_163");
            put("mostlyArmless", "5_121");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalMasterQuiGonStatsMatchBothOfficialPrintings() {
        var s = scenario(); var bp = s.GetLSCard("qui").getBlueprint();
        assertEquals("Master Qui-Gon", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(1, bp.getDestiny(), s.epsilon); assertEquals(7, bp.getDeployCost(), s.epsilon);
        assertEquals(6, bp.getPower(), s.epsilon); assertEquals(7, bp.getAbility(), s.epsilon);
        assertEquals(7, bp.getForfeit(), s.epsilon); assertTrue(bp.hasPersona(Persona.QUIGON));
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.JEDI_MASTER)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR,
                Icon.JEDI_MASTER, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    private void deploy(boolean episode, int cost) {
        var s = scenario(); s.StartGame(); var qui = s.GetLSCard("qui");
        var location = episode ? s.GetLSCard("naboo") : s.GetLSStartingLocation();
        if (episode) s.MoveLocationToTable(location);
        s.MoveCardsToHand(qui); s.LSActivateForceCheat(10);
        s.SkipToLSTurn(Phase.DEPLOY);
        int initial = s.GetLSForcePileCount();
        assertTrue(s.LSDeployAvailable(qui));
        s.LSDeployCard(qui); s.LSChooseCard(location); s.PassAllResponses();
        assertEquals(location, qui.getAtLocation()); assertEquals(cost, initial - s.GetLSForcePileCount());
    }

    @Test
    public void ActualEpisodeIDeploymentCostsSix() { deploy(true, 6); }

    @Test
    public void OtherDeploymentCostsSeven() { deploy(false, 7); }

    private boolean action(VirtualTableScenario s, String label) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("qui"), label);
    }

    private VirtualTableScenario lossToEffect(boolean credits) {
        var s = scenario(); s.StartGame();
        s.MoveLocationToTable(s.GetLSCard("chamber"));
        s.MoveCardsToLocation(s.GetLSCard("chamber"), s.GetLSCard("qui"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard("trooper"));
        s.MoveCardsToDSSideOfTable(s.GetDSCard("search"));
        if (credits) s.MoveCardsToLSSideOfTable(s.GetLSCard("credits"));
        s.MoveCardsToHand(s.GetLSCard("token"), s.GetLSCard("otherToken"));
        assertEquals(s.DS, s.GetCurrentPlayer());
        s.SkipToPhase(Phase.DRAW);
        assertTrue(s.GetCurrentDecision().getText(), s.AwaitingDSDrawPhaseActions());
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("oldInterrupt"), s.GetLSCard("control"), s.GetLSCard("episode"));
        s.PrepareLSDestiny(2);
        assertTrue(s.DSCardActionAvailable(s.GetDSCard("search"), "lose 1 Force"));
        s.DSUseCardAction(s.GetDSCard("search"), "lose 1 Force");
        s.PassAllResponses();
        assertTrue(s.AwaitingLSForceLossPayment()); s.LSPayForceLossFromReserveDeck();
        for (int i = 0; i < 25 && !action(s, UPLOAD) && !action(s, STACK); i++) {
            String prompt = s.GetCurrentDecision().getText();
            assertTrue(prompt, prompt.contains("Optional responses"));
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertTrue("An actual loss to the opponent's Effect should offer the response", action(s, UPLOAD));
        assertEquals(credits, action(s, STACK));
        return s;
    }

    @Test
    public void OpponentEffectLossUploadsEpisodeIInterruptExceptControlAndReshuffles() {
        var s = lossToEffect(true); var target = s.GetLSCard("control");
        s.gameState().markCardsRevealedFromPile(List.of(target));
        s.LSUseCardAction(s.GetLSCard("qui"), UPLOAD); s.PassAllResponses();
        assertSelectable(s, "control", false); assertSelectable(s, "oldInterrupt", false);
        assertSelectable(s, "episode", true);
        s.LSChooseCard(s.GetLSCard("episode")); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(s.GetLSCard("episode")));
        assertFalse(s.gameState().isCardRevealedFromPile(target));
        assertFalse(action(s, STACK));
    }

    private void assertSelectable(VirtualTableScenario s, String label, boolean expected) {
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(label).getBlueprintId(true));
        assertTrue(index >= 0);
        assertEquals(Boolean.toString(expected), s.LSGetADParam("selectable")[index]);
    }

    @Test
    public void OpponentEffectLossMayStackFromHandInsteadOfUploading() {
        var s = lossToEffect(true); var token = s.GetLSCard("token"); var credits = s.GetLSCard("credits");
        s.LSUseCardAction(s.GetLSCard("qui"), STACK); s.PassAllResponses();
        assertTrue(s.GetCurrentDecision().getText(), s.LSDecisionAvailable("to stack"));
        s.LSChooseCard(token); s.PassAllResponses();
        assertEquals(credits, token.getStackedOn());
        assertTrue(s.GetLSHand().contains(s.GetLSCard("otherToken")));
        assertFalse(s.GetLSHand().contains(token));
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("episode")));
        assertFalse(action(s, UPLOAD));
    }

    @Test
    public void NoCreditsOnlyOffersTheReserveAction() {
        var s = lossToEffect(false);
        assertTrue(action(s, UPLOAD)); assertFalse(action(s, STACK));
    }

    @Test
    public void ActualForceDrainLossDoesNotOfferEitherResponse() {
        var s = scenario(); s.StartGame();
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("qui"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
        s.MoveCardsToLSSideOfTable(s.GetLSCard("credits"));
        s.SkipToDSTurn(Phase.CONTROL); s.DSForceDrainAt(s.GetDSStartingLocation()); s.PassAllResponses();
        assertTrue(s.AwaitingLSForceLossPayment()); s.LSPayForceLossFromReserveDeck();
        for (int i = 0; i < 20 && s.IsActiveForceDrain(); i++) {
            assertFalse(action(s, UPLOAD)); assertFalse(action(s, STACK));
            String prompt = s.GetCurrentDecision().getText();
            assertTrue(prompt, prompt.contains("Optional responses"));
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertFalse(s.IsActiveForceDrain());
    }

    private VirtualTableScenario armed() {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation();
        s.MoveCardsToLocation(site, s.GetLSCard("qui"), s.GetLSCard("luke"), s.GetDSCard("vader"));
        s.AttachCardsTo(s.GetLSCard("qui"), s.GetLSCard("quiWeapon"));
        s.AttachCardsTo(s.GetLSCard("luke"), s.GetLSCard("lukeWeapon"));
        s.AttachCardsTo(s.GetDSCard("vader"), s.GetDSCard("vaderWeapon"));
        s.MoveCardsToHand(s.GetDSCard("disarmed"), s.GetDSCard("beaten"));
        s.DSActivateForceCheat(4); s.SkipToDSTurn(Phase.CONTROL);
        return s;
    }

    private void assertTarget(VirtualTableScenario s, PhysicalCardImpl target, boolean allowed) {
        assertEquals(target.toString(), allowed, s.DSGetCardChoices().contains(Integer.toString(target.getCardId())));
    }

    @Test
    public void DisarmedCannotSelectQuiGonButActuallyDisarmsLuke() {
        var s = armed();
        s.DSDeployCard(s.GetDSCard("disarmed")); s.PassAllResponses();
        assertTarget(s, s.GetLSCard("qui"), false); assertTarget(s, s.GetLSCard("luke"), true);
        s.DSChooseCard(s.GetLSCard("luke")); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("lukeWeapon")));
        assertEquals(s.GetLSCard("qui"), s.GetLSCard("quiWeapon").getAttachedTo());
    }

    @Test
    public void ProtectionAlsoRejectsDisarmingFromOtherNamedEffects() {
        var s = armed(); var otherEffect = s.GetDSCard("mostlyArmless");
        var canDisarm = Filters.canBeTargetedBy(otherEffect, TargetingReason.TO_BE_DISARMED);
        assertFalse(canDisarm.accepts(s.game(), s.GetLSCard("qui")));
        assertTrue(canDisarm.accepts(s.game(), s.GetLSCard("luke")));
    }

    @Test
    public void ActualYouAreBeatenTargetChoiceExcludesQuiGon() {
        var s = armed(); var beaten = s.GetDSCard("beaten");
        s.DSPlayCard(beaten, "Prevent character from moving or battling"); s.PassAllResponses();
        assertTarget(s, s.GetLSCard("qui"), false); assertTarget(s, s.GetLSCard("luke"), true);
        s.DSChooseCard(s.GetLSCard("luke")); s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(beaten));
    }

    @Test
    public void HighActualBattleDestinyDoesNotRequireQuiGonToForfeitForAttrition() {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation(); var qui = s.GetLSCard("qui");
        s.MoveCardsToLocation(site, qui, s.GetDSCard("vader"));
        s.LSStartBattleAndSkipToWeaponsSegment(site);
        s.PrepareLSDestiny(7); s.PrepareDSDestiny(7);
        s.SkipToDamageSegment(true);
        assertEquals("Vader's real battle destiny still generates attrition", 8, s.GetUnpaidLSAttrition());
        for (int i = 0; i < 35 && s.gameState().getBattleState() != null; i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (s.DSDecisionAvailable("card from battle to forfeit")) s.DSChooseCard(s.GetDSCard("vader"));
            else if (s.AwaitingLSBattleDamagePayment()) s.LSChooseCard(s.GetTopOfLSReserveDeck());
            else if (s.LSDecisionAvailable("forfeit (if desired)")) s.LSChooseCards();
            else {
                assertTrue("Qui-Gon should not be required to forfeit: " + prompt,
                        prompt.contains("Optional responses") || prompt.contains("action to play or Pass"));
                s.PlayerPass(s.GetDecidingPlayer());
            }
        }
        assertNull(s.gameState().getBattleState());
        assertEquals(site, qui.getAtLocation());
    }
}
