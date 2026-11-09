package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_062_Tests {
    private String id = "1261_62";

    private VirtualTableScenario scenario() { return scenario(false, false); }

    private VirtualTableScenario scenario(boolean ownStarter, boolean opposingStarter) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("baragwin", id); put("baragwin2", "1261_62"); put("momaw", "1_20"); put("gungan", "12_7");
            put("luke", "1_19"); put("threepio", "1_5"); put("speeder", "3_69"); put("falcon", "1_143");
            put("system", "1_135"); put("throne", "1_138"); put("traffic", "1_66"); if (ownStarter) put("starter", "13_5");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("ephant", "6_101"); if (opposingStarter) put("starter", "13_69"); }}, 60, 60,
                StartingSetup.LSStartingLocation("1_136"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void BaragwinStatsMatchFinalMasterInsteadOfOriginal() {
        var scn = scenario(); var b = scn.GetLSCard("baragwin").getBlueprint();
        assertEquals("Baragwin", b.getTitle());
        // Current card = PC card list text (PC-E1, 2012); the release-sheet text is 2261_62 (Legacy batch B32).
        assertEquals("This character is lost if a Starting Effect on table. Your non-alien characters at sites are power and forfeit = 0. If present at a battleground site, your Force drains at battleground sites where you have two aliens are +1, and whenever you deploy an alien, retrieve 1 Force.", b.getGameText()); assertTrue(b.hasVirtualSuffix()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.RESTRICTED_3, b.getUniqueness()); assertNull(b.getCardSubtype());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(2, b.getDeployCost(), scn.epsilon);
        assertEquals(1, b.getPower(), scn.epsilon); assertEquals(2, b.getAbility(), scn.epsilon); assertEquals(5, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.ALIEN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.ALIEN, Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.SCAVENGER)));
        assertEquals(Species.BARAGWIN, b.getSpecies());
    }

    private VirtualTableScenario start(boolean battleground, boolean enclosed) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("throne"));
        assertTrue(Filters.battleground_site.accepts(scn.game(), scn.GetLSStartingLocation()));
        assertFalse(Filters.battleground.accepts(scn.game(), scn.GetLSCard("throne")));
        var site = battleground ? scn.GetLSStartingLocation() : scn.GetLSCard("throne");
        scn.MoveCardsToLocation(site, scn.GetLSCard("baragwin"));
        if (enclosed) {
            scn.MoveCardsToLocation(site, scn.GetLSCard("speeder"));
            scn.BoardAsPassenger(scn.GetLSCard("speeder"), scn.GetLSCard("baragwin"));
        }
        return scn;
    }

    @Test public void BaragwinResetsOwnRebelsAndDroidsAtSitesButNotAliensOrOpponents() {
        var scn = start(true, false); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("threepio"), scn.GetLSCard("momaw"),
                scn.GetDSCard("vader"), scn.GetDSCard("ephant"));
        assertEquals(0, scn.GetPower(scn.GetLSCard("luke"))); assertEquals(0, scn.GetForfeit(scn.GetLSCard("luke")));
        assertEquals(0, scn.GetPower(scn.GetLSCard("threepio"))); assertEquals(0, scn.GetForfeit(scn.GetLSCard("threepio")));
        assertEquals(1, scn.GetPower(scn.GetLSCard("momaw"))); assertEquals(3, scn.GetForfeit(scn.GetLSCard("momaw")));
        assertEquals(6, scn.GetPower(scn.GetDSCard("vader"))); assertEquals(2, scn.GetPower(scn.GetDSCard("ephant")));
        assertEquals(1, scn.GetPower(scn.GetLSCard("baragwin"))); assertEquals(5, scn.GetForfeit(scn.GetLSCard("baragwin")));
    }

    @Test public void BaragwinResetAppliesEvenWhenNotAtBattleground() {
        var scn = start(false, false); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        assertEquals(0, scn.GetPower(scn.GetLSCard("luke"))); assertEquals(0, scn.GetForfeit(scn.GetLSCard("luke")));
    }

    @Test public void BaragwinCurrentTextDoesNotResetPilotAtSystem() {
        // PC-E1 (card list 2012): "Your non-alien characters at sites are power and forfeit = 0".
        var scn = start(true, false); var system = scn.GetLSCard("system"); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, scn.GetLSCard("falcon")); scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("luke"));
        assertEquals(3, scn.GetPower(scn.GetLSCard("luke"))); assertEquals(7, scn.GetForfeit(scn.GetLSCard("luke")));
    }

    @Test public void OriginalBaragwinAlsoResetsPilotAtSystemAndRestoresPowerWhenLeavingPlay() {
        // Release sheet 2009 (2261_62; the Master List reprints it): "Your non-alien characters are power and forfeit = 0" (not only at sites).
        id = "2261_62";
        var scn = start(true, false); var system = scn.GetLSCard("system"); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, scn.GetLSCard("falcon")); scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("luke"));
        assertEquals(0, scn.GetPower(scn.GetLSCard("luke"))); assertEquals(0, scn.GetForfeit(scn.GetLSCard("luke")));
        scn.MoveCardsToLSHand(scn.GetLSCard("baragwin")); assertEquals(3, scn.GetPower(scn.GetLSCard("luke")));
        assertEquals(7, scn.GetForfeit(scn.GetLSCard("luke")));
    }

    @Test public void BaragwinResetIncludesNonAliensInsideEnclosedVehicleAtSite() {
        var scn = start(true, false); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("speeder"));
        scn.BoardAsPassenger(scn.GetLSCard("speeder"), scn.GetLSCard("luke"));
        assertEquals(0, scn.GetPower(scn.GetLSCard("luke"))); assertEquals(0, scn.GetForfeit(scn.GetLSCard("luke")));
        assertEquals(3, scn.GetPower(scn.GetLSCard("speeder")));
    }

    @Test public void BaragwinResetChangesActualBattlePower() {
        var scn = start(true, false); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader")); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.SkipToEndOfPowerSegment(false);
        assertEquals(1, scn.GetLSTotalPower());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED"); assertEquals(5, scn.GetUnpaidLSBattleDamage());
    }

    private void drain(boolean sourceAtBattleground, boolean enclosed, boolean twoAliens, boolean nonBattlegroundTarget, int expected) {
        var scn = start(sourceAtBattleground, enclosed);
        var target = nonBattlegroundTarget ? scn.GetLSCard("throne") : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(target, scn.GetLSCard("momaw"));
        if (twoAliens) scn.MoveCardsToLocation(target, scn.GetLSCard("gungan"));
        else scn.MoveCardsToLocation(target, scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.CONTROL); int lost = scn.GetDSLostPileCount();
        scn.LSForceDrainAt(target); scn.PassForceDrainStartResponses();
        if (expected == 0) assertFalse(scn.AwaitingDSForceLossPayment());
        else scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lost + expected, scn.GetDSLostPileCount());
    }
    @Test public void BaragwinBoostsActualRemoteBattlegroundDrainWithTwoAliens() { drain(true, false, true, false, 2); }
    @Test public void BaragwinDoesNotBoostDrainWithOnlyOneAlien() { drain(true, false, false, false, 1); }
    @Test public void BaragwinNeedsToBePresentAtABattleground() { drain(false, false, true, false, 1); }
    @Test public void BaragwinInsideEnclosedVehicleDoesNotEnableDrainBonus() { drain(true, true, true, false, 1); }
    @Test public void BaragwinDoesNotBoostNonBattlegroundSite() { drain(true, false, true, true, 0); }

    private void retrieval(boolean sourceAtBattleground, boolean enclosed, boolean opponentDeploys, int expected) {
        var scn = start(sourceAtBattleground, enclosed);
        if (opponentDeploys) {
            scn.MoveCardsToDSHand(scn.GetDSCard("ephant")); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSActivateForceCheat(8);
        } else {
            scn.MoveCardsToLSHand(scn.GetLSCard("momaw")); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(5);
        }
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(3)); int lost = scn.GetLSLostPileCount();
        if (opponentDeploys) { scn.DSDeployCard(scn.GetDSCard("ephant")); scn.DSChooseCard(scn.GetDSStartingLocation()); }
        else { scn.LSDeployCard(scn.GetLSCard("momaw")); scn.LSChooseCard(scn.GetDSStartingLocation()); }
        scn.PassAllResponses(); assertEquals(lost - expected, scn.GetLSLostPileCount());
        assertInZone(expected > 0 ? Zone.USED_PILE : Zone.LOST_PILE, scn.GetLSFiller(3));
    }
    @Test public void BaragwinRetrievesOneWhenOwnAlienDeploysAtAnotherSite() { retrieval(true, false, false, 1); }
    @Test public void BaragwinDoesNotRetrieveWhenOpponentDeploysAlien() { retrieval(true, false, true, 0); }
    @Test public void BaragwinOffBattlegroundDoesNotRetrieve() { retrieval(false, false, false, 0); }
    @Test public void BaragwinNotPresentInsideEnclosedVehicleDoesNotRetrieve() { retrieval(true, true, false, 0); }

    private void deploymentWithEffect(boolean darkStarter, boolean ordinaryEffect, boolean addLater) {
        var scn = scenario(!ordinaryEffect && !darkStarter, darkStarter); scn.StartGame(); var throne = scn.GetLSCard("throne"); scn.MoveLocationToTable(throne);
        var effect = darkStarter ? scn.GetDSCard("starter") : scn.GetLSCard(ordinaryEffect ? "traffic" : "starter");
        if (addLater) scn.MoveCardsToLocation(throne, scn.GetLSCard("baragwin"));
        scn.MoveCardsToSideOfTable(effect);
        var deploy = scn.GetLSCard(addLater ? "momaw" : "baragwin"); scn.MoveCardsToLSHand(deploy);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(5); scn.LSDeployCard(deploy); scn.LSChooseCard(throne);
        scn.PassAllResponses();
        if (ordinaryEffect) assertAtLocation(throne, scn.GetLSCard("baragwin"));
        else assertInZone(Zone.LOST_PILE, scn.GetLSCard("baragwin"));
    }
    @Test public void BaragwinIsLostAfterDeploymentWithOwnStartingEffect() { deploymentWithEffect(false, false, false); }
    @Test public void BaragwinIsLostAfterDeploymentWithOpposingStartingEffect() { deploymentWithEffect(true, false, false); }
    @Test public void BaragwinDoesNotConfuseOrdinaryEffectWithStartingEffect() { deploymentWithEffect(false, true, false); }
    @Test public void BaragwinAlreadyInPlayIsLostWhenStartingEffectIsOnTable() { deploymentWithEffect(false, false, true); }
    @Test public void BaragwinRetrievesForItsOwnActualDeploymentAtBattleground() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLSHand(scn.GetLSCard("baragwin"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(5); scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(3));
        scn.LSDeployCard(scn.GetLSCard("baragwin")); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("baragwin")); assertEquals(2, scn.GetLSLostPileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSFiller(3));
    }

    @Test public void BaragwinDoesNotRetrieveForOwnNonAlienDeployment() {
        var scn = start(true, false); scn.MoveCardsToLSHand(scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(5); scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(3));
        scn.LSDeployCard(scn.GetLSCard("luke")); scn.LSChooseCard(scn.GetDSStartingLocation()); scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("luke")); assertEquals(3, scn.GetLSLostPileCount());
    }

}
