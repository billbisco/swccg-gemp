package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.timing.results.PutCardToHandFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * The "card put into hand from off table" result records the action that took the card, so a card can tell which
 * card's game text took it into hand (TriggerConditions.justPutIntoHandFromOffTableBy).
 */
public class PutCardToHandFromOffTableResultTests {

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); }},
                new HashMap<>() {{ put("failure", "1261_36"); put("vader", "1_168"); put("trooper", "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void resultExposesTheActionThatTookTheCard() {
        var scn = scenario();
        scn.StartGame();
        var failure = scn.GetDSCard("failure");
        var vader = scn.GetDSCard("vader");
        var action = new TopLevelGameTextAction(failure, scn.DS, failure.getCardId());
        var result = new PutCardToHandFromOffTableResult(action, scn.DS, vader);
        assertSame(action, result.getAction());
        assertSame(failure, result.getAction().getActionSource());
    }

    @Test
    public void justPutIntoHandFromOffTableByMatchesCardAndSourceOnly() {
        var scn = scenario();
        scn.StartGame();
        var game = scn.game();
        var failure = scn.GetDSCard("failure");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var result = new PutCardToHandFromOffTableResult(new TopLevelGameTextAction(failure, scn.DS, failure.getCardId()), scn.DS, vader);

        assertTrue(TriggerConditions.justPutIntoHandFromOffTableBy(game, result, vader, Filters.title("Protocol Failure")));
        // Different card taken into hand
        assertFalse(TriggerConditions.justPutIntoHandFromOffTableBy(game, result, trooper, Filters.title("Protocol Failure")));
        // Taken by a different source
        var byTrooper = new PutCardToHandFromOffTableResult(new TopLevelGameTextAction(trooper, scn.DS, trooper.getCardId()), scn.DS, vader);
        assertFalse(TriggerConditions.justPutIntoHandFromOffTableBy(game, byTrooper, vader, Filters.title("Protocol Failure")));
        // Different result type
        assertFalse(TriggerConditions.justPutIntoHandFromOffTableBy(game,
                new RemovedFromCardPileResult(new TopLevelGameTextAction(failure, scn.DS, failure.getCardId())), vader, Filters.any));
    }
}
