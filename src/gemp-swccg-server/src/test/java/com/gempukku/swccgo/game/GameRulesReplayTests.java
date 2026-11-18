package com.gempukku.swccgo.game;

import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GameRulesReplayTests {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void GameRulesReplayStoresTheExactSetupAlongsidePlayableEvents() throws Exception {
        var format = VirtualTableScenario._formatLibrary.getFormat("open");
        var setup = GameSetup.of(format, Map.of(GameOptions.OPERATIVE_RULE, "false"), GameOption.Scope.CASUAL);
        String summary = setup.summaryJson(format);
        var mediator = mock(SwccgGameMediator.class);
        when(mediator.getPlayersPlaying()).thenReturn(java.util.List.of(
                new SwccgGameParticipant("Light", GuestDecks.deck(PlayNowSide.LIGHT)),
                new SwccgGameParticipant("Dark", GuestDecks.deck(PlayNowSide.DARK))));
        when(mediator.getGameSetup()).thenReturn(setup);
        when(mediator.getGameRulesSummary()).thenReturn(summary);
        doAnswer(call -> {
            ((GameStateListener) call.getArgument(1)).sendMessage("Game started");
            return null;
        }).when(mediator).addGameStateListener(anyString(), any(GameStateListener.class));
        var recorder = new GameRecorder(mock(GameHistoryService.class), null, null, tmp.getRoot());
        recorder.recordGame(mediator, "Open", "", Map.of("Light", "light", "Dark", "dark"))
                .finishRecording("Light", "Won", "Dark", "Lost");
        try (var files = Files.walk(tmp.getRoot().toPath())) {
            var recordings = files.filter(Files::isRegularFile).toList();
            assertEquals(2, recordings.size());
            for (var file : recordings) {
                try (var input = new InflaterInputStream(Files.newInputStream(file))) {
                    var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
                    assertEquals("gameReplay", doc.getDocumentElement().getTagName());
                    assertEquals(summary, doc.getDocumentElement().getAttribute("rules"));
                    assertEquals(1, doc.getElementsByTagName("ge").getLength());
                }
            }
        }
    }
}
