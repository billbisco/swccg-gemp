package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_023_Tests {
    private static final String DOWNLOAD = "Deploy a squadron there";

    private boolean IsSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] ids = scn.GetADParam(scn.DS, "cardId");
        String[] blueprints = scn.GetADParam(scn.DS, "blueprintId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        assertNotNull(ids);
        assertNotNull(selectable);
        assertEquals(ids.length, selectable.length);
        for (int i = 0; i < ids.length; i++) {
            boolean sameCard = ids[i].equals(String.valueOf(card.getCardId()))
                    || (ids[i].startsWith("temp") && blueprints != null && blueprints[i].equals(card.getBlueprintId(true)));
            if (sameCard) return Boolean.parseBoolean(selectable[i]);
        }
        return false;
    }

    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("corvette", "1_140"); put("alter", "1_71"); put("luke", "1_19");
            put("hoth", "3_55"); put("site", "1_133");
        }}, new HashMap<>() {{
            put("dreaded", "1240_23"); put("capital", "106_13"); put("carrier", "106_13");
            put("isd", "1_302"); put("victory", "2_155"); put("fighter", "1_304");
            put("squadron", "9_172"); put("replacement", "2_153"); put("wrongIcon", "7_303");
            put("hoth", "3_143"); put("deathStar", "2_143"); put("rendili", "7_287");
            put("yards", "7_238"); put("piett", "9_98"); put("terminal", "1_228");
        }}, 40, 40, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void DreadedImperialStarfleetPrintedStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("dreaded").getBlueprint();
        assertEquals("Dreaded Imperial Starfleet", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("The Empire uses short-range fighters and patrol craft to maintain a presence in every system within its scope of power.", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4)));
    }

    @Test
    public void DreadedImperialStarfleetDeploysOnTableWithoutAForceCost() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("dreaded"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(scn.GetDSCard("dreaded"));
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("dreaded"));
        assertEquals(force, scn.GetDSForcePileCount());
    }

    private VirtualTableScenario Ready(boolean moreSystems) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("dreaded"));
        if (moreSystems) scn.MoveLocationToTable(scn.GetDSCard("hoth"));
        scn.MoveCardsToDSHand(scn.GetDSCard("capital"), scn.GetDSCard("isd"),
                scn.GetDSCard("victory"), scn.GetDSCard("fighter"));
        scn.EnsureDSForcePile(15);
        return scn;
    }

    private void DeployCapitalFor(VirtualTableScenario scn, String key, int expected) {
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard(key));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard(key));
        assertEquals(force - expected, scn.GetDSForcePileCount());
    }

    @Test
    public void DreadedImperialStarfleetReducesCapitalDeploymentByTwoWithMoreSystems() {
        DeployCapitalFor(Ready(true), "capital", 3);
    }

    @Test
    public void DreadedImperialStarfleetDoesNotReduceDeploymentWhenSystemCountsAreTied() {
        DeployCapitalFor(Ready(false), "capital", 5);
    }

    @Test
    public void DreadedImperialStarfleetDoesNotReduceDeploymentWhenOpponentHasMoreSystems() {
        var scn = Ready(false);
        scn.MoveLocationToTable(scn.GetLSCard("hoth"));
        DeployCapitalFor(scn, "capital", 5);
    }

    @Test
    public void DreadedImperialStarfleetDoesNotReduceDeploymentToANonBattleground() {
        var scn = Ready(true);
        scn.MoveLocationToTable(scn.GetDSCard("deathStar"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("capital"));
        scn.DSChooseCard(scn.GetDSCard("deathStar"));
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSCard("deathStar"), scn.GetDSCard("capital"));
        assertEquals(force - 5, scn.GetDSForcePileCount());
    }

    @Test
    public void DreadedImperialStarfleetDoesNotReduceStarfighterDeployment() {
        var scn = Ready(true);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("fighter"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("fighter"));
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void DreadedImperialStarfleetAllowsFinalCostBelowThreeWithinItsReductionCap() {
        var scn = Ready(true);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("carrier"));
        scn.BoardAsPilot(scn.GetDSCard("carrier"), scn.GetDSCard("piett"));
        DeployCapitalFor(scn, "capital", 2);
    }

    @Test
    public void DreadedImperialStarfleetCapsCombinedReductionsAtThree() {
        var scn = Ready(false);
        scn.MoveLocationToTable(scn.GetDSCard("rendili"));
        scn.AttachCardsTo(scn.GetDSCard("rendili"), scn.GetDSCard("yards"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("victory"));
        scn.DSChooseCard(scn.GetDSCard("rendili"));
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSCard("rendili"), scn.GetDSCard("victory"));
        assertEquals(force - 3, scn.GetDSForcePileCount());
    }

    @Test
    public void DreadedImperialStarfleetStopsReducingDeploymentWhenItLeavesPlay() {
        var scn = Ready(true);
        scn.MoveCardsToDSHand(scn.GetDSCard("dreaded"));
        DeployCapitalFor(scn, "capital", 5);
    }

    private VirtualTableScenario SquadronReady() {
        var scn = Ready(true);
        scn.MoveOutOfPlay(scn.GetDSCard("squadron"));
        scn.MoveOutOfPlay(scn.GetDSCard("replacement"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("squadron"), scn.GetDSCard("replacement"),
                scn.GetDSCard("wrongIcon"), scn.GetDSCard("victory"));
        scn.DSDeployCard(scn.GetDSCard("isd"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        for (int i = 0; i < 40; i++) {
            if (scn.DSAnyActionsAvailable() && scn.DSCardActionAvailable(scn.GetDSCard("dreaded"), DOWNLOAD)) return scn;
            assertTrue(scn.GetCurrentDecision().getText(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Squadron download was not offered after deployment");
        return scn;
    }

    @Test
    public void DreadedImperialStarfleetMayDownloadADeathStarIISquadronThereForFree() {
        var scn = SquadronReady();
        int force = scn.GetDSForcePileCount();
        var order = new ArrayList<>(scn.gameState().getReserveDeck(scn.DS));
        order.remove(scn.GetDSCard("squadron"));
        scn.gameState().setRandomSeed(124023);
        scn.DSUseCardAction(scn.GetDSCard("dreaded"), DOWNLOAD);
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("squadron"));
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("squadron"));
        assertEquals(force, scn.GetDSForcePileCount());
        assertNotEquals(order, new ArrayList<>(scn.gameState().getReserveDeck(scn.DS)));
    }

    @Test
    public void DreadedImperialStarfleetDownloadRequiresBothSquadronTypeAndAPrintedSetIcon() {
        var scn = SquadronReady();
        scn.DSUseCardAction(scn.GetDSCard("dreaded"), DOWNLOAD);
        scn.PassAllResponses();
        assertTrue(IsSelectable(scn, scn.GetDSCard("squadron")));
        assertTrue(IsSelectable(scn, scn.GetDSCard("replacement")));
        assertFalse(IsSelectable(scn, scn.GetDSCard("wrongIcon")));
        assertFalse(IsSelectable(scn, scn.GetDSCard("victory")));
    }

    @Test
    public void DreadedImperialStarfleetMayDownloadANewHopeSquadronWithoutReplacingShips() {
        var scn = SquadronReady();
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(scn.GetDSCard("dreaded"), DOWNLOAD);
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("replacement"));
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("replacement"));
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("isd"));
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void DreadedImperialStarfleetMayDeclineItsSquadronDownload() {
        var scn = SquadronReady();
        scn.PassAllResponses();
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("squadron"), scn.GetDSCard("replacement"));
        assertTrue(scn.AwaitingDSDeployPhaseActions() || scn.AwaitingLSDeployPhaseActions());
    }

    @Test
    public void DreadedImperialStarfleetDoesNotDownloadAfterDeployingAnotherCapitalModel() {
        var scn = Ready(true);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("squadron"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("capital"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        for (int i = 0; i < 40 && scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"); i++) {
            if (scn.DSAnyActionsAvailable()) assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("dreaded"), DOWNLOAD));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions() || scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.DSAnyActionsAvailable() && scn.DSCardActionAvailable(scn.GetDSCard("dreaded"), DOWNLOAD));
    }

    @Test
    public void DreadedImperialStarfleetDoesNotReduceOpponentsCapitalDeployment() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("dreaded"));
        scn.MoveLocationToTable(scn.GetDSCard("hoth"));
        scn.MoveCardsToLSHand(scn.GetLSCard("corvette"));
        scn.EnsureLSForcePile(5);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("corvette"));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("corvette"));
        assertEquals(force - 4, scn.GetLSForcePileCount()); // Original Corvette1_140 deploys4.
    }

    @Test
    public void DreadedImperialStarfleetCannotBeTargetedByAlterWhileAnOrdinaryEffectCan() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("site"));
        scn.MoveCardsToLocation(scn.GetLSCard("site"), scn.GetLSCard("luke"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("dreaded"), scn.GetDSCard("terminal"));
        scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
        scn.EnsureLSForcePile(5);
        scn.SkipToPhase(Phase.CONTROL);
        scn.LSPlayCard(scn.GetLSCard("alter"));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("terminal")));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("dreaded")));
    }
}
