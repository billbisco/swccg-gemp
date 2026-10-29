package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import static org.junit.Assert.*;

public class ComponentCounterSaveTests {
    @Test public void PrintedComponentCountersRoundTripThroughFreshCodec() {
        var game = new BotGame(42);
        var bots = BotGame.bots("ozzel", "yoda");
        while (game.canContinue() && !(game.turns() >= 2 && QuietPoint.isQuiet(game.game, game.feedback))) {
            game.botStep(bots);
            assertTrue(game.turns() < 20);
        }
        // Populate serialization input explicitly; gameplay recording is covered by ComponentUniquenessTests.
        var component = new TitleComponent("Snapshot Component", Uniqueness.RESTRICTED_2);
        var query = game.game.getModifiersQuerying();
        query.getTitleComponentPlayedTurnLimitCounter(component).incrementToLimit(2, 1);
        query.getCardTitlePlayedTurnLimitCounter(component.name()).incrementToLimit(2, 1);
        var codec = new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current());
        var bytes = codec.write(new SaveRoot(game.game, game.feedback, bots));
        var restored = new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current()).read(bytes);
        var restoredQuery = restored.game().getModifiersQuerying();
        assertEquals(1, restoredQuery.getTitleComponentPlayedTurnLimitCounter(new TitleComponent("SNAPSHOT COMPONENT", Uniqueness.RESTRICTED_2)).getUsedLimit());
        assertEquals(1, restoredQuery.getCardTitlePlayedTurnLimitCounter(component.name()).getUsedLimit());
        assertEquals(0, restoredQuery.getTitleComponentPlayedTurnLimitCounter(new TitleComponent(component.name(), Uniqueness.UNIQUE)).getUsedLimit());
        query.getTitleComponentPlayedTurnLimitCounter(component).incrementToLimit(2, 1);
        assertEquals(1, restoredQuery.getTitleComponentPlayedTurnLimitCounter(component).getUsedLimit());
    }
}
