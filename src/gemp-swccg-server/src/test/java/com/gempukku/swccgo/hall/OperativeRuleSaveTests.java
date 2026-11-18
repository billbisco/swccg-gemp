package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.save.GameSaveCodec;
import com.gempukku.swccgo.game.save.GameSaveHeader;
import com.gempukku.swccgo.game.save.InMemoryGameSaveDAO;
import com.gempukku.swccgo.game.save.SaveBuild;
import com.gempukku.swccgo.game.save.SavedGameManager;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowSide;
import org.junit.Test;

import java.time.Clock;
import java.util.Map;

import static org.junit.Assert.*;

public class OperativeRuleSaveTests {
    private GameSaveCodec codec() {
        return new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current());
    }

    private PlayNowSaves saves(GameSaveCodec codec) {
        return new PlayNowSaves(new InMemoryGameSaveDAO(), codec, Clock.systemUTC(), SavedGameManager.Settings.defaults(),
                null, null, VirtualTableScenario._formatLibrary, VirtualTableScenario._cardLibrary, false, null, null);
    }

    private GameSaveHeader header(boolean chosen) {
        var header = PlayNowSaves.header("operative-save", "player", PlayNowSide.LIGHT, PlayNowBot.OZZEL, 1234);
        if (chosen) {
            var format = VirtualTableScenario._formatLibrary.getFormat(header.formatCode);
            header.gameSetup = GameSetup.of(format, Map.of(GameOptions.OPERATIVE_RULE, "false"), GameOption.Scope.BOT).toJson();
        }
        return header;
    }

    @Test
    public void OperativeRuleOldHeaderKeepsTheRuleEnabled() {
        var codec = codec();
        var header = GameSaveHeader.fromJson(header(false).toJson());
        assertNull(header.gameSetup);
        assertTrue(saves(codec).newGame(header).game().getGameState().isOperativeRuleEnabled());
    }

    @Test
    public void OperativeRuleMoveLogFactoryUsesSavedOptionBeforeStarting() {
        var codec = codec();
        var header = GameSaveHeader.fromJson(header(true).toJson());
        assertNotNull(header.gameSetup);
        assertFalse(saves(codec).newGame(header).game().getGameState().isOperativeRuleEnabled());
    }

    @Test
    public void OperativeRuleSnapshotRetainsTheChosenRule() {
        var codec = codec();
        var root = saves(codec).newGame(header(true));
        var restored = codec.read(codec.write(root));
        assertFalse(restored.game().getGameState().isOperativeRuleEnabled());
    }
}
