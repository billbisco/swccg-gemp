package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.save.*;
import com.gempukku.swccgo.game.setup.*;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.time.Clock;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProjectiveTelepathySaveTests {
    @Test
    public void ProjectiveTelepathySavedChoiceRebuildsAndSnapshotsTheSelectedPhysicalCard() throws Exception {
        var formats = VirtualTableScenario._formatLibrary;
        var library = VirtualTableScenario._cardLibrary;
        var codec = new GameSaveCodec(library, formats, SaveBuild.current());
        var saves = new PlayNowSaves(new InMemoryGameSaveDAO(), codec, Clock.systemUTC(), SavedGameManager.Settings.defaults(),
                null, null, formats, library, false, null, null);
        for (String choice : List.of("printed", "current")) {
            var header = PlayNowSaves.header("telepathy-save", "player", PlayNowSide.LIGHT, PlayNowBot.OZZEL, 1234);
            var setup = GameSetup.of(formats.getFormat(header.formatCode),
                    Map.of(GameOptions.PROJECTIVE_TELEPATHY, choice), GameOption.Scope.BOT);
            header.gameSetup = setup.toJson();
            var bot = header.seats.get(1);
            var savedDeck = new SwccgDeck("Imported original PT");
            savedDeck.addCard("5_149o");
            while (savedDeck.getCards().size() < 60) savedDeck.addCard("1_167");
            header.seats.set(1, GameSaveHeader.seat(bot.playerId, bot.displayName, bot.bot, savedDeck));
            var restoredHeader = GameSaveHeader.fromJson(header.toJson());
            var root = saves.newGame(restoredHeader);
            String expected = choice.equals("printed") ? "5_149o" : "5_149";
            restoredHeader.kryoClassList = codec.getBuild().getClassListFingerprint();
            var snapshotSave = new GameSave(restoredHeader, new DecisionLog(), codec.write(root), 0,
                    codec.getBuild().getClassFingerprint());
            var resumer = new GameResumer(codec, saves);
            var snapshot = resumer.resume(snapshotSave);
            assertEquals(GameResumer.Method.SNAPSHOT, snapshot.method());
            var moves = resumer.resume(new GameSave(restoredHeader, new DecisionLog(), null, 0, null));
            assertEquals(GameResumer.Method.MOVE_LOG, moves.method());
            for (var game : List.of(root.game(), snapshot.root().game(), moves.root().game())) {
                assertEquals(expected, game.getFormat().resolveCardId("5_149o"));
                var card = game.getGameState().getAllPermanentCards().stream()
                        .filter(c -> c.getTitle().equals("Projective Telepathy")).findFirst().orElseThrow();
                assertEquals(expected, card.getBlueprintId(true));
            }
            assertEquals("5_149o", restoredHeader.seats.get(1).cards.getFirst());
            assertEquals("5_149o", savedDeck.getCards().getFirst());
        }
    }
}
