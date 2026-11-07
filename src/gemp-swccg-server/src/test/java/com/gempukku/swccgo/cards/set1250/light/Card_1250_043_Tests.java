package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_043_Tests {
    private static final String CHOOSE = "Draw two battle destiny and choose one";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("williams", "1250_43"); put("radiant", "12_92"); put("tantive", "2_73"); put("luke", "1_19");
        }}, new HashMap<>() {{ put("devastator", "1_301"); }}, 35, 35,
                StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalWilliamsHasPrintedRepublicStatsAndIcons() {
        var s = scenario(); var bp = s.GetLSCard("williams").getBlueprint();
        assertEquals(Title.Williams, bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(3, bp.getDestiny(), s.epsilon); assertEquals(2, bp.getDeployCost(), s.epsilon);
        assertEquals(2, bp.getPower(), s.epsilon); assertEquals(2, bp.getAbility(), s.epsilon);
        assertEquals(4, bp.getForfeit(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.REPUBLIC)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.REPUBLIC, Icon.CORUSCANT, Icon.EPISODE_I,
                Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void ActualPilotSlotAddsTwoPowerButPassengerDoesNot() {
        var piloted = battle("radiant", true, false);
        assertEquals(7, piloted.GetPower(piloted.GetLSCard("radiant")));

        var s = scenario(); s.StartGame(); var ship = s.GetLSCard("tantive");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), ship);
        int base = s.GetPower(ship);
        s.BoardAsPassenger(ship, s.GetLSCard("williams")); assertEquals(base, s.GetPower(ship));
    }

    private VirtualTableScenario battle(String piloted, boolean sameSystem, boolean extraAbility) {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation(); var other = s.GetDSStartingLocation();
        var ship = s.GetLSCard(piloted); var williams = s.GetLSCard("williams"); var destroyer = s.GetDSCard("devastator");
        s.MoveCardsToLocation(sameSystem ? site : other, ship); s.BoardAsPilot(ship, williams);
        if (!sameSystem) s.MoveCardsToLocation(site, s.GetLSCard("tantive"));
        if (extraAbility) s.BoardAsPilot(sameSystem ? ship : s.GetLSCard("tantive"), s.GetLSCard("luke"));
        s.MoveCardsToLocation(site, destroyer); s.LSActivateForceCheat(6);
        s.SkipToLSTurn(Phase.BATTLE);
        s.LSInitiateBattle(site);
        return s;
    }

    @Test
    public void ActualRadiantPilotDrawsOneBelowNormalAbilityWithoutAddingWhenEligible() {
        assertEquals(1, battle("radiant", true, false).GetLSBattleDestinyCount());
        assertEquals(1, battle("radiant", true, true).GetLSBattleDestinyCount());
    }

    private boolean chooseOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("williams"), CHOOSE);
    }

    private void passUntilChooseOffered(VirtualTableScenario s) {
        for (int i = 0; i < 18 && !chooseOffered(s)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
    }

    @Test
    public void ActualReplacementAtOwnSystemDrawsTwoAndUsesSelectedPhysicalDestiny() {
        var s = battle("radiant", true, false); var high = s.GetLSDestiny(5); var low = s.GetLSDestiny(2);
        s.MoveCardsToTopOfLSReserveDeck(high, low); s.SkipToPowerSegment();
        assertTrue(s.LSDecisionAvailable("battle destiny?")); s.LSChooseYes(); passUntilChooseOffered(s);
        assertTrue(chooseOffered(s)); s.LSUseCardAction(s.GetLSCard("williams"), CHOOSE);
        s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertTrue(s.LSDecisionAvailable("Choose 1 destiny"));
        assertTrue(s.LSHasCardChoicesAvailable(high, low)); s.LSChooseCard(low);
        s.PassAllResponses(); s.SkipBattleDestinyDraws(false);
        assertFalse(s.LSDecisionAvailable("into hand"));
        assertEquals(2, s.GetLSTotalDestiny());
        assertTrue(s.GetLSUsedPile().containsAll(List.of(high, low)));
        assertFalse(s.GetLSHand().contains(high));
    }

    private VirtualTableScenario battleAtOpponentsSystem() {
        var s = scenario(); s.StartGame(); var system = s.GetDSStartingLocation();
        var ship = s.GetLSCard("radiant");
        s.MoveCardsToLocation(system, ship); s.BoardAsPilot(ship, s.GetLSCard("williams"));
        s.MoveCardsToLocation(system, s.GetDSCard("devastator")); s.LSActivateForceCheat(6);
        s.SkipToLSTurn(Phase.BATTLE);
        s.LSInitiateBattle(system);
        return s;
    }

    private void drawTwoAtOpponentsSystem(boolean takeIntoHand) {
        var s = battleAtOpponentsSystem(); var high = s.GetLSDestiny(5); var low = s.GetLSDestiny(2);
        s.MoveCardsToTopOfLSReserveDeck(high, low); s.SkipToPowerSegment();
        assertTrue(s.LSDecisionAvailable("battle destiny?")); s.LSChooseYes(); passUntilChooseOffered(s);
        assertTrue(chooseOffered(s)); s.LSUseCardAction(s.GetLSCard("williams"), CHOOSE);
        s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertTrue(s.LSDecisionAvailable("Choose 1 destiny")); s.LSChooseCard(low);
        assertTrue(s.LSDecisionAvailable("into hand"));
        if (takeIntoHand) s.LSChooseYes(); else s.LSChooseNo();
        s.PassAllResponses(); s.SkipBattleDestinyDraws(false);
        assertEquals(2, s.GetLSTotalDestiny());
        assertEquals(takeIntoHand, s.GetLSHand().contains(high));
        assertEquals(!takeIntoHand, s.GetLSUsedPile().contains(high));
        assertTrue(s.GetLSUsedPile().contains(low));
    }

    @Test
    public void AtOpponentsSystemMayTakeTheOtherActualDestinyIntoHand() { drawTwoAtOpponentsSystem(true); }

    @Test
    public void AtOpponentsSystemMayDeclineAndTheOtherDestinyIsHandledAsNormal() { drawTwoAtOpponentsSystem(false); }

    @Test
    public void WilliamsAtAnotherSystemCannotReplaceActualBattleDestiny() {
        var s = battle("radiant", false, true); s.PrepareLSDestiny(3); s.SkipToPowerSegment();
        assertTrue(s.LSDecisionAvailable("battle destiny?")); s.LSChooseYes(); passUntilChooseOffered(s);
        assertFalse(chooseOffered(s));
        s.PassAllResponses(); s.SkipBattleDestinyDraws(false); assertEquals(3, s.GetLSTotalDestiny());
    }
}
