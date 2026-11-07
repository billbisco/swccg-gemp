package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.framework.TestBase.DS;
import static com.gempukku.swccgo.framework.TestBase.LS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StartingEpicEventTests {
    @Test
    public void anEpicEventDeployedByAStartingInterruptRemainsRecordedAfterLeavingPlay() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>(Map.of(
                "interrupt", "217_21", "epic", "217_20", "castle", "209_50")));
        var epic = scn.GetDSCard("epic");
        scn.DSDecided("0");
        scn.LSDecided("0");
        StartingSetup.DefaultDSGroundLocation.Setup(scn);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);

        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(DS));
        scn.DSChooseCard(scn.GetDSCard("interrupt"));
        if (scn.DSHasCardChoiceAvailable(epic)) scn.DSChooseCard(epic);

        assertEquals(Zone.SIDE_OF_TABLE, epic.getZone());
        assertTrue(scn.gameState().hasPlayedStartingEpicEvent(DS));
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(LS));
        scn.MoveOutOfPlay(epic);
        assertTrue(scn.gameState().hasPlayedStartingEpicEvent(DS));
    }

    @Test
    public void startingLocationsAndOtherCardsDoNotCountAsEpicEvents() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
        scn.StartGame();

        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(DS));
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(LS));
    }

    @Test
    public void anEpicEventPlayResultDuringATurnDoesNotCountAsStartingPlay() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("epic", "2_42")), new HashMap<>());
        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        var epic = scn.GetLSCard("epic");

        scn.game().getActionsEnvironment().emitEffectResult(
                new PlayCardResult(LS, epic, Zone.HAND, null, null, null, true, false));

        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(LS));
    }
}
