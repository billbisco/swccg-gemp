package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_105_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1240_102");
                    put("room", "1240_105");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RebelCellSituationRoomStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("room").getBlueprint();

        assertEquals("Rebel Cell - Situation Room", card.getTitle());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.A_NEW_HOPE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelCellSituationRoomDoesNotDeployWithoutHiddenLandingSite() {
        var scn = GetScenario();
        var room = scn.GetLSCard("room");

        scn.StartGame();
        scn.MoveCardsToLSHand(room);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(room));
    }

    @Test
    public void RebelCellSituationRoomDeploysToSamePlanetAsHiddenLandingSite() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("room", "1240_105");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var hls = scn.GetLSCard("hls");
        var room = scn.GetLSCard("room");

        scn.StartGame();
        scn.MoveCardsToLSHand(room);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertEquals(Zone.LOCATIONS, hls.getZone());
        assertEquals("Yavin 4", hls.getPartOfSystem());
        assertTrue(scn.LSDeployAvailable(room));
        scn.LSDeployCard(room);
        if (!scn.LSDecisionAvailable("On which side")) {
            scn.LSChooseCard(hls);
        }
        assertTrue(scn.LSDecisionAvailable("On which side"));
        scn.LSChoose("Left");
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, room.getZone());
        assertEquals("Yavin 4", room.getPartOfSystem());
    }

    @Test
    public void RebelCellSituationRoomDeploysToTatooineWithHiddenLandingSite() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var room = scn.GetLSCard("room");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls, room);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hls));
        scn.LSDeployLocation(hls);
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());

        assertTrue(scn.LSDeployAvailable(room));
        scn.LSDeployCard(room);
        scn.LSChooseCard(hls);
        assertTrue(scn.LSDecisionAvailable("On which side"));
        scn.LSChoose("Left");
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, room.getZone());
        assertEquals("Tatooine", room.getPartOfSystem());
    }

    @Test
    public void RebelCellSituationRoomAddsPowerAtRelatedSystemPerYourCharacter() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1240_102");
                    put("room", "1240_105");
                    put("tat", "1_127");
                    put("luke", "1_19");
                    put("biggs", "1_3");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var hls = scn.GetLSCard("hls");
        var room = scn.GetLSCard("room");
        var tat = scn.GetLSCard("tat");
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(hls);
        scn.MoveLocationToTable(room);

        float p0 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), tat, scn.LS, false, false);
        scn.MoveCardsToLocation(room, luke);
        float p1 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), tat, scn.LS, false, false);
        scn.MoveCardsToLocation(room, biggs);
        float p2 = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), tat, scn.LS, false, false);
        scn.MoveCardsToOpponentLocation(room, trooper);
        float pOpp = scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), tat, scn.LS, false, false);

        assertEquals(Math.round(p0) + 1, Math.round(p1));
        assertEquals(Math.round(p0) + 2, Math.round(p2));
        assertEquals(Math.round(p2), Math.round(pOpp));
    }

    @Test
    public void RebelCellSituationRoomOnTatooineIsNotInsideCantinaGroup() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1240_102");
                    put("room", "1240_105");
                    put("cantina", "1_128");
                    put("db94", "1_129");
                    put("mos", "1_133");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var hls = scn.GetLSCard("hls");
        var room = scn.GetLSCard("room");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        var mos = scn.GetLSCard("mos");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(mos);
        scn.MoveLocationToTable(hls);
        scn.MoveLocationToTable(room);

        var order = scn.gameState().getLocationsInOrder();
        int cantinaIdx = order.indexOf(cantina);
        int mosIdx = order.indexOf(mos);
        int roomIdx = order.indexOf(room);
        int low = Math.min(cantinaIdx, mosIdx);
        int high = Math.max(cantinaIdx, mosIdx);
        assertTrue(roomIdx >= 0);
        assertFalse(roomIdx > low && roomIdx < high);
        assertEquals("Tatooine", room.getPartOfSystem());

        var offers = scn.gameState().getLocationPlacement(scn.game(), room, "Tatooine", null);
        for (LocationPlacement placement : offers) {
            PhysicalCard other = placement.getOtherCard();
            if (other == null) {
                continue;
            }
            int otherIdx = order.indexOf(other);
            LocationPlacementDirection direction = placement.getDirection();
            if (direction.isLeftOf()) {
                assertFalse(otherIdx > low && otherIdx <= high);
            }
            if (direction.isRightOf()) {
                int insertIdx = otherIdx + 1;
                assertFalse(insertIdx > low && insertIdx <= high);
            }
        }
    }

    @Test
    public void RebelCellSituationRoomOnYavin4IsNotBetweenDockingBayAndMassassiHeadquarters() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("room", "1240_105");
                    put("db", "1_136");
                    put("hq", "7_134");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var db = scn.GetLSCard("db");
        var hq = scn.GetLSCard("hq");
        var room = scn.GetLSCard("room");
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        var dbPlacement = scn.gameState().getLocationPlacement(scn.game(), db, "Yavin 4", null);
        scn.MoveLocationToTable(db, dbPlacement.getFirst());
        var hqPlacement = scn.gameState().getLocationPlacement(scn.game(), hq, "Yavin 4", null);
        scn.MoveLocationToTable(hq, hqPlacement.getFirst());

        var order = scn.gameState().getLocationsInOrder();
        assertNoOfferedPlacementBetween(scn, room, "Yavin 4", order, db, hq);

        var roomPlacement = scn.gameState().getLocationPlacement(scn.game(), room, "Yavin 4", null);
        scn.MoveLocationToTable(room, roomPlacement.getFirst());

        order = scn.gameState().getLocationsInOrder();
        int dbIdx = order.indexOf(db);
        int hqIdx = order.indexOf(hq);
        int roomIdx = order.indexOf(room);
        int hlsIdx = order.indexOf(hls);
        int low = Math.min(dbIdx, hqIdx);
        int high = Math.max(dbIdx, hqIdx);
        assertTrue(roomIdx >= 0);
        assertFalse(roomIdx > low && roomIdx < high);
        assertTrue(hqIdx > Math.min(dbIdx, hlsIdx) && hqIdx < Math.max(dbIdx, hlsIdx));
        assertEquals(roomIdx > hqIdx, hlsIdx > hqIdx);
    }

    @Test
    public void RebelCellSituationRoomOnGenericPlanetIsAtInteriorExteriorBoundary() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("corellia", "2_61");
                    put("hls", "1240_102");
                    put("room", "1240_105");
                    put("jungle", "7_122");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var corellia = scn.GetLSCard("corellia");
        var hls = scn.GetLSCard("hls");
        var room = scn.GetLSCard("room");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(corellia);
        var hlsPlacement = scn.gameState().getLocationPlacement(scn.game(), hls, "Corellia", null);
        scn.MoveLocationToTable(hls, hlsPlacement.getFirst());
        var junglePlacement = scn.gameState().getLocationPlacement(scn.game(), jungle, "Corellia", null);
        scn.MoveLocationToTable(jungle, junglePlacement.getFirst());

        var order = scn.gameState().getLocationsInOrder();
        assertNoOfferedPlacementBetween(scn, room, "Corellia", order, hls, jungle);

        var roomPlacement = scn.gameState().getLocationPlacement(scn.game(), room, "Corellia", null);
        scn.MoveLocationToTable(room, roomPlacement.getFirst());

        order = scn.gameState().getLocationsInOrder();
        int hlsIdx = order.indexOf(hls);
        int jungleIdx = order.indexOf(jungle);
        int roomIdx = order.indexOf(room);
        int low = Math.min(hlsIdx, jungleIdx);
        int high = Math.max(hlsIdx, jungleIdx);
        assertTrue(roomIdx >= 0);
        assertFalse(roomIdx > low && roomIdx < high);
        assertEquals("Corellia", room.getPartOfSystem());
    }

    @Test
    public void RebelCellSituationRoomBufferDockingBayPlacementUnchanged() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("db", "1_136");
                    put("hq", "7_134");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var db = scn.GetLSCard("db");
        var hq = scn.GetLSCard("hq");
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        var dbPlacement = scn.gameState().getLocationPlacement(scn.game(), db, "Yavin 4", null);
        scn.MoveLocationToTable(db, dbPlacement.getFirst());
        var hqPlacement = scn.gameState().getLocationPlacement(scn.game(), hq, "Yavin 4", null);
        scn.MoveLocationToTable(hq, hqPlacement.getFirst());

        var order = scn.gameState().getLocationsInOrder();
        int dbIdx = order.indexOf(db);
        int hqIdx = order.indexOf(hq);
        int hlsIdx = order.indexOf(hls);
        assertTrue(hqIdx > Math.min(dbIdx, hlsIdx) && hqIdx < Math.max(dbIdx, hlsIdx));
    }

    private void assertNoOfferedPlacementBetween(VirtualTableScenario scn, PhysicalCard room, String system,
                                                 List<? extends PhysicalCard> order, PhysicalCard a, PhysicalCard b) {
        int low = Math.min(order.indexOf(a), order.indexOf(b));
        int high = Math.max(order.indexOf(a), order.indexOf(b));
        var offers = scn.gameState().getLocationPlacement(scn.game(), room, system, null);
        for (LocationPlacement placement : offers) {
            PhysicalCard other = placement.getOtherCard();
            if (other == null) {
                continue;
            }
            int otherIdx = order.indexOf(other);
            LocationPlacementDirection direction = placement.getDirection();
            if (direction.isLeftOf()) {
                assertFalse(otherIdx > low && otherIdx <= high);
            }
            if (direction.isRightOf()) {
                int insertIdx = otherIdx + 1;
                assertFalse(insertIdx > low && insertIdx <= high);
            }
        }
    }

    StartingSetup HiddenBaseVObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hiddenbase", "1240_107");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSDecisionAvailable("starting")) {
                scn.LSChooseCard(scn.GetLSCard("hiddenbase"));
            }
            for (int i = 0; i < 20; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("battleground")
                        || scn.LSDecisionAvailable("Choose card to deploy from Reserve Deck")) {
                    scn.LSChooseCard(scn.GetLSCard("yavin"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Hidden Landing Site")) {
                    scn.LSChooseCard(scn.GetLSCard("hls"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Uncharted Settlements")) {
                    scn.LSChooseCard(scn.GetLSCard("uncharted"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Choose pair")
                        || scn.LSDecisionAvailable("Choose pair you may deploy")) {
                    scn.LSChoose("Rebel / Rebel starship");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };
}
