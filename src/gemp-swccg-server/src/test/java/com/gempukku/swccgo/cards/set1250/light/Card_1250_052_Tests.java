package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class Card_1250_052_Tests {
    private static final String DEPLOY = "Lose 1 Force to deploy a Rebel from Lost Pile";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("padme", "1250_52"); put("bail", "1116_1"); put("senate", "12_75");
            put("leia", "1_17"); put("han", "1_11"); put("luke", "1_19"); put("guard", "1_26");
            put("maintenance", "13_9"); put("alien", "7_50"); put("payment", "1_109");
        }}, new HashMap<>() {{ put("vader", "1_168"); }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalPadmeHasBothAffiliationsAndBothAgendasAtOnce() {
        var s = scenario(); var card = s.GetLSCard("padme"); var bp = card.getBlueprint();
        assertEquals("Senator Padme Amidala", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("Leader. Senator.", bp.getLore()); assertTrue(bp.hasPersona(Persona.AMIDALA));
        assertEquals(1, bp.getDestiny(), s.epsilon); assertEquals(3, bp.getDeployCost(), s.epsilon);
        assertEquals(3, bp.getPower(), s.epsilon); assertEquals(4, bp.getAbility(), s.epsilon);
        assertEquals(4, bp.getPolitics(), s.epsilon); assertEquals(7, bp.getForfeit(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.REBEL, CardType.REPUBLIC)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.REBEL, Icon.REPUBLIC, Icon.EPISODE_I,
                Icon.WARRIOR, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.LEADER, Keyword.SENATOR)));
        s.StartGame(); s.MoveCardsToLocation(s.GetLSStartingLocation(), card);
        assertEquals(Set.of(Agenda.JUSTICE, Agenda.REBELLION),
                Set.copyOf(s.game().getModifiersQuerying().getAgendas(s.gameState(), card)));
    }

    private VirtualTableScenario setup(boolean senate, boolean bailWithPadme) {
        var s = scenario(); s.StartGame(); var home = s.GetLSStartingLocation(); var chamber = s.GetLSCard("senate");
        s.MoveLocationToTable(chamber);
        var padmeSite = senate ? chamber : home;
        var otherSite = senate ? home : chamber;
        s.MoveCardsToLocation(padmeSite, s.GetLSCard("padme"));
        s.MoveCardsToLocation(bailWithPadme ? padmeSite : otherSite, s.GetLSCard("bail"));
        s.MoveCardsToHand(s.GetLSCard("payment")); s.LSActivateForceCheat(8);
        s.MoveCardsToTopOfLSLostPile(s.GetLSCard("leia"), s.GetLSCard("han"), s.GetLSCard("luke"),
                s.GetLSCard("guard"), s.GetLSCard("maintenance"), s.GetLSCard("alien"));
        s.SkipToLSTurn(Phase.DEPLOY);
        return s;
    }

    private boolean offered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("padme"), DEPLOY);
    }

    @Test
    public void BailMustBeWithPadmeAtTheActualSenate() {
        assertFalse(offered(setup(true, false)));
        assertFalse(offered(setup(false, true)));
        assertTrue(offered(setup(true, true)));
    }

    private void selectable(VirtualTableScenario s, String card, boolean expected) {
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(card).getBlueprintId(true));
        assertTrue(card, index >= 0);
        assertEquals(card, Boolean.toString(expected), s.LSGetADParam("selectable")[index]);
    }

    @Test
    public void PaidActualLostPileDeploymentChecksEveryPrintedBoundaryAndUsesNormalForceCost() {
        var s = setup(true, true); var padme = s.GetLSCard("padme"); var leia = s.GetLSCard("leia");
        var payment = s.GetLSCard("payment"); var home = s.GetLSStartingLocation();
        int force = s.GetLSForcePileCount();
        assertTrue(offered(s)); s.LSUseCardAction(padme, DEPLOY); s.PassAllResponses();
        assertTrue(s.AwaitingLSForceLossPayment()); s.LSChooseCard(payment); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(payment));
        selectable(s, "leia", true); selectable(s, "han", true); selectable(s, "luke", false);
        selectable(s, "guard", false); selectable(s, "maintenance", false); selectable(s, "alien", false);
        s.LSChooseCard(leia); s.PassAllResponses();
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(home);
        s.PassAllResponses();
        assertEquals(home, leia.getAtLocation()); assertFalse(s.GetLSLostPile().contains(leia));
        assertEquals(force - leia.getBlueprint().getDeployCost(), s.GetLSForcePileCount(), s.epsilon);
        if (s.AwaitingDSDeployPhaseActions()) s.DSPass();
        assertTrue(s.AwaitingLSDeployPhaseActions()); assertFalse(offered(s));

        // The use limit resets, but deployment still follows the normal deploy phase.
        s.SkipToDSTurn(Phase.CONTROL);
        assertTrue(s.AwaitingDSControlPhaseActions());
        s.DSPass();
        assertFalse(offered(s));
        s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.AwaitingLSDeployPhaseActions());
        assertTrue(offered(s));
        var secondPayment = s.GetTopOfLSReserveDeck();
        var han = s.GetLSCard("han");
        force = s.GetLSForcePileCount();
        s.LSUseCardAction(padme, DEPLOY);
        s.PassAllResponses();
        assertTrue(s.AwaitingLSForceLossPayment());
        s.LSChooseCard(secondPayment);
        s.PassAllResponses();
        s.LSChooseCard(han);
        s.PassAllResponses();
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(home);
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(secondPayment));
        assertFalse(s.GetLSLostPile().contains(han));
        assertEquals(home, han.getAtLocation());
        assertEquals(force - han.getBlueprint().getDeployCost(), s.GetLSForcePileCount(), s.epsilon);
    }

    @Test
    public void LostFromSameSiteAsVaderGoesOutOfPlayAndOpponentLosesFour() {
        var scn = scenario(); scn.StartGame();
        var site = scn.GetDSStartingLocation(); var padme = scn.GetLSCard("padme");
        scn.MoveCardsToLocation(site, padme, scn.GetDSCard("vader"));
        scn.EnsureDSForcePile(1);
        scn.SkipToPhase(Phase.BATTLE);
        int dsForceBefore = scn.GetDSLifeForceRemaining();
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        scn.LSPayBattleDamageFromCardInPlay(padme);
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, padme.getZone());
        assertFalse(scn.GetLSLostPile().contains(padme));
        // Dark Side chooses where each of the 4 Force comes from.
        for (int i = 0; i < 12 && scn.GetDSLifeForceRemaining() > dsForceBefore - 4; i++) {
            var choice = scn.GetForcePile(scn.DS).isEmpty() ? null : scn.GetTopOfDSForcePile();
            if (choice == null || !scn.DSHasCardChoiceAvailable(choice))
                choice = scn.GetUsedPile(scn.DS).isEmpty() ? null : scn.GetTopOfDSUsedPile();
            if (choice == null || !scn.DSHasCardChoiceAvailable(choice)) choice = scn.GetTopOfDSReserveDeck();
            assertTrue("Unexpected decision: " + scn.DSGetDecision().getText(), scn.DSHasCardChoiceAvailable(choice));
            scn.DSChooseCard(choice);
            scn.PassAllResponses();
        }
        assertEquals(dsForceBefore - 4, scn.GetDSLifeForceRemaining());
    }

    @Test
    public void LostAwayFromVaderStaysInLostPileAndOpponentLosesNothing() {
        var scn = scenario(); scn.StartGame();
        var site = scn.GetDSStartingLocation(); var padme = scn.GetLSCard("padme");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(site, padme, scn.GetDSFiller(1), scn.GetDSFiller(2), scn.GetDSFiller(3),
                scn.GetDSFiller(4), scn.GetDSFiller(5));
        scn.EnsureDSForcePile(1);
        scn.SkipToPhase(Phase.BATTLE);
        int dsForceBefore = scn.GetDSLifeForceRemaining();
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        scn.LSPayBattleDamageFromCardInPlay(padme);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(padme));
        assertEquals(dsForceBefore, scn.GetDSLifeForceRemaining());
    }
}
