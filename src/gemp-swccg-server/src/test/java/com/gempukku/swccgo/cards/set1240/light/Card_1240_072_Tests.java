package com.gempukku.swccgo.cards.set1240.light;

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

public class Card_1240_072_Tests {
    private VirtualTableScenario GetScenario() {
        StartingSetup shields = new StartingSetup() {
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("s1", "13_1"); put("s2", "13_3"); put("s3", "13_4"); put("s4", "13_6");
                }};
            }
            public void Setup(VirtualTableScenario scn) { }
        };
        return new VirtualTableScenario(new HashMap<>() {{
            put("leia", "1240_72"); put("luke", "1_19"); put("fear", "13_5");
            // The named ship has no original Decipher printing: use the real OVS card, not a fabricated substitute.
            put("consular", "1115_2"); put("corvette", "1_140"); put("corvette2", "1_140");
            put("yavin", "1_135");
        }}, new HashMap<>() {{
            put("lateral", "1_222"); put("dantooine", "1_282"); put("chimaera", "9_154");
            put("motti", "1_164"); put("trooper", "1_194"); put("evazan", "1_172");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                shields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SenatorLeiaOrganaPrintedIdentityStatsIconsAndCharacteristicsAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetLSCard("leia").getBlueprint();
        assertEquals("Senator Leia Organa", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(Side.LIGHT, bp.getSide());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(3, bp.getPower(), scn.epsilon);
        assertEquals(3, bp.getAbility(), scn.epsilon);
        assertEquals(5, bp.getForfeit(), scn.epsilon);
        assertEquals(1, bp.getPolitics(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertTrue(bp.hasPersona(Persona.LEIA));
        assertEquals(Species.ALDERAANIAN, bp.getSpecies());
        assertEquals(CardCategory.CHARACTER, bp.getCardCategory());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.REBEL, CardType.REPUBLIC)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.REBEL, Icon.REPUBLIC, Icon.WARRIOR, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.SENATOR, Keyword.LEADER, Keyword.FEMALE)));
    }

    private VirtualTableScenario Ready(boolean light, Phase phase) {
        var scn = GetScenario();
        scn.StartGame();
        if (light) scn.SkipToLSTurn(Phase.ACTIVATE);
        else scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.EnsureLSForcePile(10);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToLSHand(scn.GetLSCard("leia"));
        scn.SkipToPhase(phase);
        return scn;
    }

    private void DeployLeia(VirtualTableScenario scn) {
        scn.LSDeployCard(scn.GetLSCard("leia"));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassForceUseResponses();
        scn.PassCardPlayResponses();
    }

    private void SetupShields(VirtualTableScenario scn) {
        var fear = scn.GetLSCard("fear");
        scn.MoveCardsToLSSideOfTable(fear);
        scn.StackCardsOn(fear, scn.GetLSCard("s1"), scn.GetLSCard("s2"), scn.GetLSCard("s3"), scn.GetLSCard("s4"));
    }

    private void ReachShield(VirtualTableScenario scn) {
        for (int i = 0; i < 40; i++) {
            if (scn.LSAnyActionsAvailable() && scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Defensive Shield")) return;
            assertNotNull(scn.GetCurrentDecision());
            assertTrue(scn.GetCurrentDecision().getText(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Leia's just-deployed shield permission missing");
    }

    /** Passes the responses to Leia's deployment, asserting her shield permission is never offered. */
    private void PassDeploymentWithoutShield(VirtualTableScenario scn) {
        for (int i = 0; i < 40 && scn.GetCurrentDecision() != null
                && scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"); i++) {
            if (scn.LSDecisionAvailable("optional response"))
                assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Defensive Shield"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    @Test
    public void SenatorLeiaOrganaHasJusticeAndRebellionAgendasWhileActive() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        var agendas = scn.game().getModifiersQuerying().getAgendas(scn.gameState(), scn.GetLSCard("leia"));
        assertEquals(2, agendas.size());
        assertTrue(agendas.contains(Agenda.JUSTICE));
        assertTrue(agendas.contains(Agenda.REBELLION));
    }

    @Test
    public void SenatorLeiaOrganaActuallyDeploysForThreeForce() {
        var scn = Ready(true, Phase.ACTIVATE);
        LeaveActivateWithForce(scn, true, 3);
        scn.SkipToPhase(Phase.DEPLOY);
        DeployLeia(scn);
        scn.PassAllResponses();
        assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("leia").getAtLocation());
    }

    @Test
    public void SenatorLeiaOrganaCannotDeployWithOnlyTwoForce() {
        var scn = Ready(true, Phase.ACTIVATE);
        LeaveActivateWithForce(scn, true, 2);
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(scn.GetLSCard("leia")));
        assertInZone(Zone.HAND, scn.GetLSCard("leia"));
    }

    @Test
    public void SenatorLeiaOrganaMayPlayShieldFromOwnStartingEffectWhenActuallyDeployed() {
        var scn = Ready(true, Phase.DEPLOY);
        SetupShields(scn);
        DeployLeia(scn);
        ReachShield(scn);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("leia"), "Defensive Shield");
        scn.LSChooseCard(scn.GetLSCard("s4"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSCard("s4").getZone().isInPlay());
        assertEquals(force, scn.GetLSForcePileCount());
        assertFalse(scn.IsStackedOn(scn.GetLSCard("fear"), scn.GetLSCard("s4")));
    }

    @Test
    public void SenatorLeiaOrganaMayDeclineShieldLeavingItStacked() {
        var scn = Ready(true, Phase.DEPLOY);
        SetupShields(scn);
        DeployLeia(scn);
        ReachShield(scn);
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(scn.GetLSCard("fear"), scn.GetLSCard("s4")));
    }

    @Test
    public void SenatorLeiaOrganaCannotPlayShieldFromEmptyStartingEffect() {
        var scn = Ready(true, Phase.DEPLOY);
        // The scenario's Starting Effect begins with the four shields under it: empty it first.
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSCard("s1"), scn.GetLSCard("s2"), scn.GetLSCard("s3"), scn.GetLSCard("s4"));
        assertTrue(scn.GetLSCard("fear").getZone().isInPlay());
        DeployLeia(scn);
        PassDeploymentWithoutShield(scn);
        assertTrue(scn.GetLSCard("leia").getZone().isInPlay());
    }

    @Test
    public void SenatorLeiaOrganaShieldPlayDoesNotConsumeStartingEffectPrintedUses() {
        var scn = Ready(true, Phase.DEPLOY);
        SetupShields(scn);
        DeployLeia(scn);
        ReachShield(scn);
        scn.LSUseCardAction(scn.GetLSCard("leia"), "Defensive Shield");
        scn.LSChooseCard(scn.GetLSCard("s4"));
        scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.CONTROL);
        for (String name : List.of("s1", "s2", "s3")) {
            AwaitLSControl(scn);
            assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("fear"), "Defensive Shield"));
            scn.LSUseCardAction(scn.GetLSCard("fear"), "Defensive Shield");
            scn.LSChooseCard(scn.GetLSCard(name));
            scn.PassAllResponses();
            assertTrue(scn.GetLSCard(name).getZone().isInPlay());
        }
    }

    @Test
    public void SenatorLeiaOrganaMayPlayAnotherShieldOnAnotherActualDeployment() {
        var scn = Ready(true, Phase.DEPLOY);
        SetupShields(scn);
        DeployLeia(scn);
        ReachShield(scn);
        scn.LSUseCardAction(scn.GetLSCard("leia"), "Defensive Shield");
        scn.LSChooseCard(scn.GetLSCard("s4"));
        scn.PassAllResponses();
        scn.MoveCardsToLSHand(scn.GetLSCard("leia"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        DeployLeia(scn);
        ReachShield(scn);
        scn.LSUseCardAction(scn.GetLSCard("leia"), "Defensive Shield");
        scn.LSChooseCard(scn.GetLSCard("s1"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSCard("s4").getZone().isInPlay());
        assertTrue(scn.GetLSCard("s1").getZone().isInPlay());
    }

    @Test
    public void SenatorLeiaOrganaDoesNotOfferShieldWithoutStartingEffectOrOnLaterPhase() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSCard("fear"), scn.GetLSCard("s1"), scn.GetLSCard("s2"), scn.GetLSCard("s3"), scn.GetLSCard("s4"));
        DeployLeia(scn);
        PassDeploymentWithoutShield(scn);
        SetupShields(scn);
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Defensive Shield"));
        assertTrue(scn.IsStackedOn(scn.GetLSCard("fear"), scn.GetLSCard("s4")));
    }

    private void AwaitLSControl(VirtualTableScenario scn) {
        for (int i = 0; i < 12 && !scn.AwaitingLSControlPhaseActions(); i++) scn.PlayerPass(scn.GetDecidingPlayer());
        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    @Test
    public void SenatorLeiaOrganaCanPlayFourthShieldAfterStartingEffectUsesAreExhausted() {
        var scn = Ready(true, Phase.CONTROL);
        SetupShields(scn);
        for (String name : List.of("s1", "s2", "s3")) {
            AwaitLSControl(scn);
            scn.LSUseCardAction(scn.GetLSCard("fear"), "Defensive Shield");
            scn.LSChooseCard(scn.GetLSCard(name));
            scn.PassAllResponses();
            assertTrue(scn.GetLSCard(name).getZone().isInPlay());
        }
        AwaitLSControl(scn);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("fear"), "Defensive Shield"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        DeployLeia(scn);
        ReachShield(scn);
        scn.LSUseCardAction(scn.GetLSCard("leia"), "Defensive Shield");
        // With one shield left under the Starting Effect, it is selected automatically.
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("s4"))) scn.LSChooseCard(scn.GetLSCard("s4"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSCard("s4").getZone().isInPlay());
    }

    private VirtualTableScenario SpaceReady(boolean aboardConsular) {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetDSCard("dantooine"));
        scn.MoveLocationToTable(scn.GetLSCard("yavin"));
        scn.MoveCardsToLocation(scn.GetDSCard("dantooine"), scn.GetLSCard("consular"), scn.GetLSCard("corvette"), scn.GetLSCard("corvette2"));
        scn.BoardAsPassenger(scn.GetLSCard(aboardConsular ? "consular" : "corvette"), scn.GetLSCard("leia"));
        return scn;
    }

    private boolean IsSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.DSHasCardChoiceAvailable(card);
    }

    private void PlayLateralDamage(VirtualTableScenario scn) {
        scn.MoveCardsToDSHand(scn.GetDSCard("lateral"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("lateral"));
        scn.DSChooseCard(scn.GetLSCard("yavin"));
    }

    @Test
    public void SenatorLeiaOrganaProtectsHerConsularShipFromActualLateralDamageButNotOtherShips() {
        var scn = SpaceReady(true);
        PlayLateralDamage(scn);
        assertFalse(IsSelectable(scn, scn.GetLSCard("consular")));
        assertTrue(IsSelectable(scn, scn.GetLSCard("corvette")));
        scn.DSChooseCard(scn.GetLSCard("corvette"));
        scn.PassAllResponses();
        assertEquals(0, scn.GetPower(scn.GetLSCard("corvette")));
        assertEquals(0, scn.GetForfeit(scn.GetLSCard("corvette")));
        assertEquals(5, scn.GetPower(scn.GetLSCard("consular")));
        assertEquals(8, scn.GetForfeit(scn.GetLSCard("consular")));
    }

    @Test
    public void SenatorLeiaOrganaAboardWrongShipDoesNotProtectConsularShipFromLateralDamage() {
        var scn = SpaceReady(false);
        PlayLateralDamage(scn);
        assertTrue(IsSelectable(scn, scn.GetLSCard("consular")));
        assertTrue(IsSelectable(scn, scn.GetLSCard("corvette")));
        scn.DSChooseCard(scn.GetLSCard("consular"));
        scn.PassAllResponses();
        assertEquals(0, scn.GetPower(scn.GetLSCard("consular")));
        assertEquals(0, scn.GetForfeit(scn.GetLSCard("consular")));
    }

    @Test
    public void SenatorLeiaOrganaLeavingCarrierRestoresItsLateralDamageVulnerability() {
        var scn = SpaceReady(true);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        PlayLateralDamage(scn);
        assertTrue(IsSelectable(scn, scn.GetLSCard("consular")));
        scn.DSChooseCard(scn.GetLSCard("consular"));
        scn.PassAllResponses();
        assertEquals(0, scn.GetPower(scn.GetLSCard("consular")));
    }

    private VirtualTableScenario SpaceBattleReady(boolean aboardConsular) {
        var scn = SpaceReady(aboardConsular);
        scn.MoveCardsToLSHand(scn.GetLSCard("corvette2"));
        if (aboardConsular) scn.MoveCardsToLSHand(scn.GetLSCard("corvette"));
        scn.MoveCardsToLocation(scn.GetDSCard("dantooine"), scn.GetDSCard("chimaera"));
        scn.BoardAsPilot(scn.GetDSCard("chimaera"), scn.GetDSCard("motti"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(7);
        scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(scn.GetDSCard("dantooine"));
        for (int i = 0; i < 40 && !scn.IsReachedDamageSegment(); i++) {
            if (scn.LSDecisionAvailable("destiny?")) scn.LSChooseYes();
            else if (scn.DSDecisionAvailable("destiny?")) scn.DSChooseYes();
            else scn.PassResponses();
        }
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertEquals(7, scn.GetDSTotalDestiny());
        // Light draws no battle destiny here, so there is no attrition against Dark to satisfy.
        assertEquals(0, scn.GetUnpaidDSAttrition());
        return scn;
    }

    @Test
    public void SenatorLeiaOrganaConsularShipSurvivesSevenActualAttritionAfterPayingBattleDamage() {
        var scn = SpaceBattleReady(true);
        assertEquals(7, scn.GetUnpaidLSAttrition());
        int damage = scn.GetUnpaidLSBattleDamage();
        assertTrue(damage > 0);
        int lost = scn.GetLSLostPileCount();
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals("0", scn.GetADParam(scn.LS, "min")[0]);
        scn.LSPass();
        assertEquals(lost + damage, scn.GetLSLostPileCount());
        assertEquals(scn.GetDSCard("dantooine"), scn.GetLSCard("consular").getAtLocation());
        assertTrue(scn.GetLSCard("leia").getZone().isInPlay());
    }

    @Test
    public void SenatorLeiaOrganaAboardWrongShipDoesNotGrantConsularShipAttritionImmunity() {
        var scn = SpaceBattleReady(false);
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals("1", scn.GetADParam(scn.LS, "min")[0]);
        scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("consular"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("consular"));
        assertTrue(scn.GetLSCard("leia").getZone().isInPlay());
    }

    private VirtualTableScenario GroundBattleReady(boolean leiaHere) {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetDSCard("motti"), scn.GetDSCard("evazan"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(leiaHere ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation(), scn.GetLSCard("leia"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareLSDestiny(2);
        scn.PrepareDSDestiny(1);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.SkipToDamageSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
        assertEquals(1, scn.GetDSTotalDestiny());
        return scn;
    }

    @Test
    public void SenatorLeiaOrganaAddsTwoActualAttritionAgainstOpponentHereOnly() {
        var scn = GroundBattleReady(true);
        assertEquals(4, scn.GetUnpaidDSAttrition());
        assertEquals(1, scn.GetUnpaidLSAttrition());
        if (scn.AwaitingLSAttritionPayment()) scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("luke"));
        assertTrue(scn.AwaitingDSAttritionPayment());
        scn.DSPayAttritionFromCardInPlay(scn.GetDSCard("trooper"));
        assertEquals(2, scn.GetUnpaidDSAttrition());
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("trooper"));
    }

    @Test
    public void SenatorLeiaOrganaDoesNotAddAttritionToBattleElsewhere() {
        var scn = GroundBattleReady(false);
        assertEquals(2, scn.GetUnpaidDSAttrition());
        assertEquals(1, scn.GetUnpaidLSAttrition());
    }

    /**
     * From the given player's activate phase, sets that player's Force Pile to exactly force and leaves the phase
     * without activating, so the next phase's actions are offered against that Force.
     */
    static void LeaveActivateWithForce(VirtualTableScenario scn, boolean light, int force) {
        String player = light ? scn.LS : scn.DS;
        scn.EnsureForcePile(player, force);
        scn.DrainForcePileTo(player, force);
        if (light) scn.LSPass(); else scn.DSPass();
        if (scn.DecisionAvailable(player, "You have not activated Force")) scn.ChooseOption(player, "Yes");
        scn.PassActivateActions();
    }
}
