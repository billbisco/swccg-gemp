package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgGame;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.TestBase.DS;
import static com.gempukku.swccgo.framework.TestBase.LS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StartingEpicEventTests {
    private static final String PROMPT = "Choose starting Epic Event";
    private static final String EVENT_ID = "217_20";

    @Test
    public void successfulStartingPlayRemainsRecordedAfterCardLeavesTable() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
        var event = addEpicEvent(scn, true, true);
        chooseStartingLocations(scn);

        assertTrue(scn.DSDecisionAvailable(PROMPT));
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(DS));
        scn.DSChooseCard(event);

        assertEquals(Zone.SIDE_OF_TABLE, event.getZone());
        assertTrue(scn.gameState().hasPlayedStartingEpicEvent(DS));
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(LS));
        scn.MoveOutOfPlay(event);
        assertTrue(scn.gameState().hasPlayedStartingEpicEvent(DS));
    }

    @Test
    public void decliningStartingPlayDoesNotCountAsPlaying() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
        var event = addEpicEvent(scn, true, true);
        chooseStartingLocations(scn);

        assertTrue(scn.DSDecisionAvailable(PROMPT));
        scn.DSDecided("");
        assertFalse(event.getZone().isInPlay());
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(DS));
    }

    @Test
    public void unavailableAndNonStartingEpicEventsAreSkipped() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
        var unavailable = addEpicEvent(scn, true, false);
        var ordinary = addEpicEvent(scn, false, true);
        chooseStartingLocations(scn);

        assertFalse(scn.DSDecisionAvailable(PROMPT));
        assertFalse(scn.LSDecisionAvailable(PROMPT));
        assertFalse(unavailable.getZone().isInPlay());
        assertFalse(ordinary.getZone().isInPlay());
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(DS));
    }

    @Test
    public void playingAnEpicEventDuringATurnDoesNotCountAsStartingPlay() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
        var event = addEpicEvent(scn, false, true);
        scn.StartGame();
        scn.MoveCardsToDSHand(event);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(event);
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, event.getZone());
        assertFalse(scn.gameState().hasPlayedStartingEpicEvent(DS));
    }

    private static PhysicalCardImpl addEpicEvent(VirtualTableScenario scn, boolean starting, boolean deployable) {
        SwccgCardBlueprint event = new AbstractEpicEventDeployable(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,
                "Test Epic Event", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V) {
            @Override
            public boolean isStartingEpicEvent() {
                return starting;
            }

            @Override
            protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self,
                                                               PlayCardOptionId option, boolean asReact) {
                return deployable;
            }
        };
        var library = new SwccgCardBlueprintLibrary() {
            @Override
            public SwccgCardBlueprint getSwccgoCardBlueprint(String blueprintId) {
                return EVENT_ID.equals(blueprintId) ? event : super.getSwccgoCardBlueprint(blueprintId);
            }
        };
        var card = (PhysicalCardImpl) scn.gameState().createPhysicalCard(DS, library, EVENT_ID);
        scn.gameState().addCardToZone(card, Zone.RESERVE_DECK, DS);
        return card;
    }

    private static void chooseStartingLocations(VirtualTableScenario scn) {
        scn.DSDecided("0");
        scn.LSDecided("0");
        StartingSetup.DefaultDSGroundLocation.Setup(scn);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);
    }
}
