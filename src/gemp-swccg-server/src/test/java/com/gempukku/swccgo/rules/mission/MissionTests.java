package com.gempukku.swccgo.rules.mission;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MissionTests {

    private static class TestNormalMission extends AbstractMission {
        TestNormalMission() {
            super(Side.LIGHT, 3, "Test Mission", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1, false);
        }
    }

    private static class TestCriticalMission extends AbstractMission {
        TestCriticalMission(Side side, String title) {
            super(side, 0, title, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1, true);
        }
    }

    protected VirtualTableScenario GetScenario() {
        return GetScenario(StartingSetup.DefaultLSGroundLocation);
    }

    protected VirtualTableScenario GetScenario(StartingSetup lsStart) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10,
                10,
                lsStart,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Test-only Missions are not set classes. Register the blueprint, then create the physical card through
     * GameState so it gets a real id, generic card back, and both all-cards maps.
     */
    @SuppressWarnings("unchecked")
    private PhysicalCardImpl putInHand(VirtualTableScenario scn, int id, String owner, AbstractMission blueprint) {
        String blueprintId = "2000_" + id;
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.put(blueprintId, blueprint);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        PhysicalCardImpl card = (PhysicalCardImpl) scn.gameState().createPhysicalCard(owner, VirtualTableScenario._cardLibrary, blueprintId);
        scn.gameState().addCardToZone(card, Zone.HAND, owner, true, false, false);
        return card;
    }

    private void skipToLSDeploy(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.DEPLOY);
    }

    /** After LS plays a card, Dark Side may be asked for off-player deploy actions first. */
    private void returnToLSDeploy(VirtualTableScenario scn) {
        scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
            scn.PassAllResponses();
        }
    }

    private boolean lsDeployOffered(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] cardIds = scn.GetADParam(scn.LS, "cardId");
        if (cardIds == null) {
            return false;
        }
        return scn.LSDeployAvailable(card);
    }

    @Test
    public void MissionTypeIconAndHumanReadableNames() {
        assertEquals("Mission", CardType.MISSION.getHumanReadable());
        assertEquals("Mission", CardCategory.MISSION.getHumanReadable());
        assertEquals("Mission", Icon.MISSION.getHumanReadable());
        assertEquals("Critical", CardSubtype.CRITICAL.getHumanReadable());

        var normal = new TestNormalMission();
        assertEquals(CardCategory.MISSION, normal.getCardCategory());
        assertTrue(normal.getCardTypes().contains(CardType.MISSION));
        assertEquals(CardSubtype.NORMAL, normal.getCardSubtype());
        assertTrue(normal.hasIcon(Icon.MISSION));

        var critical = new TestCriticalMission(Side.LIGHT, "Test Critical Mission");
        assertEquals(CardSubtype.CRITICAL, critical.getCardSubtype());
        assertTrue(critical.getCardTypes().contains(CardType.MISSION));
    }

    @Test
    public void NormalMissionDeploysForFreeDuringDeployPhase() {
        var scn = GetScenario();
        scn.StartGame();
        var mission = putInHand(scn, 9001, scn.LS, new TestNormalMission());

        skipToLSDeploy(scn);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(mission));
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(mission);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, mission);
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void NormalMissionNotOfferedDuringControlPhase() {
        var scn = GetScenario();
        scn.StartGame();
        var mission = putInHand(scn, 9001, scn.LS, new TestNormalMission());

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSDeployAvailable(mission));
    }

    @Test
    public void CriticalMissionDeploysWhenStartedWithoutObjective() {
        var scn = GetScenario();
        scn.StartGame();
        var mission = putInHand(scn, 9002, scn.LS, new TestCriticalMission(Side.LIGHT, "Test Critical Mission"));

        skipToLSDeploy(scn);
        assertTrue(scn.LSDeployAvailable(mission));
        scn.LSDeployCard(mission);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, mission);
        assertTrue(scn.gameState().hasPlayedCriticalMission(scn.LS));
    }

    @Test
    public void CriticalMissionNotOfferedIfStartedWithObjective() {
        var scn = GetScenario(StartingSetup.ThereIsGoodInHimObjective);
        scn.StartGame();
        var mission = putInHand(scn, 9002, scn.LS, new TestCriticalMission(Side.LIGHT, "Test Critical Mission"));

        skipToLSDeploy(scn);
        assertFalse(scn.LSDeployAvailable(mission));
    }

    @Test
    public void SecondCriticalMissionNotOfferedWhileFirstOnTable() {
        var scn = GetScenario();
        scn.StartGame();
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToHand(luke);
        var first = putInHand(scn, 9002, scn.LS, new TestCriticalMission(Side.LIGHT, "Test Critical Mission A"));
        var second = putInHand(scn, 9003, scn.LS, new TestCriticalMission(Side.LIGHT, "Test Critical Mission B"));

        skipToLSDeploy(scn);
        scn.LSDeployCard(first);
        returnToLSDeploy(scn);
        assertInZone(Zone.SIDE_OF_TABLE, first);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(luke));
        assertFalse(lsDeployOffered(scn, second));
    }

    @Test
    public void SecondCriticalMissionStillNotOfferedAfterFirstLeavesTable() {
        var scn = GetScenario();
        scn.StartGame();
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToHand(luke);
        var first = putInHand(scn, 9002, scn.LS, new TestCriticalMission(Side.LIGHT, "Test Critical Mission A"));
        var second = putInHand(scn, 9003, scn.LS, new TestCriticalMission(Side.LIGHT, "Test Critical Mission B"));

        skipToLSDeploy(scn);
        scn.LSDeployCard(first);
        returnToLSDeploy(scn);
        scn.MoveCardsToTopOfOwnLostPile(first);
        scn.PassCardLeavingTable();
        returnToLSDeploy(scn);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(luke));
        assertFalse(lsDeployOffered(scn, second));
    }

    @Test
    public void OpponentMayDeployOwnCriticalMission() {
        var scn = GetScenario();
        scn.StartGame();
        var ls = putInHand(scn, 9002, scn.LS, new TestCriticalMission(Side.LIGHT, "LS Critical Mission"));
        var ds = putInHand(scn, 9004, scn.DS, new TestCriticalMission(Side.DARK, "DS Critical Mission"));

        skipToLSDeploy(scn);
        scn.LSDeployCard(ls);
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(ds));
        scn.DSDeployCard(ds);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, ls);
        assertInZone(Zone.SIDE_OF_TABLE, ds);
    }
}
