package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.ai.AiRegistry;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgGameParticipant;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class PlayNowGameTests {
    private final PlayNowTestServer _server = new PlayNowTestServer();

    @Test
    public void PlayNowGuestGameSeatsTheGuestAsTheirSideAgainstTheBot() throws Exception {
        //test1: the guest's seat is "Light Side", the other seat is ~YodaBot, and the bot is registered for the game
        //test2: the game is a bot game in P-ANH with the bundled precons, and the guest is asked the first decision
        //test3: the registry knows the guest owns the game and which seat is theirs
        String gameId = _server.hallServer.startPlayNowGame("guest-abc", null, PlayNowSide.LIGHT, PlayNowBot.YODA, "10.0.0.1");
        SwccgGameMediator game = _server.swccgoServer.getGameById(gameId);

        List<String> seats = new ArrayList<>();
        for (SwccgGameParticipant p : game.getPlayersPlaying()) {
            seats.add(p.getPlayerId());
        }
        assertTrue(seats.contains("Light Side")); //test1
        assertTrue(seats.contains(Player.AI_ADVANCED_ID)); //test1
        assertTrue(AiRegistry.isAi(gameId, Player.AI_ADVANCED_ID)); //test1

        assertTrue(game.isBotGame()); //test2
        assertEquals("premiere_anh", game.getFormat().getCode()); //test2
        assertEquals("Light Side", game.getGameState().getLightPlayer()); //test2
        assertNotNull(pendingDecision(game, "Light Side")); //test2

        PlayNowGames.Entry entry = _server.hallServer.getPlayNowGames().get(gameId);
        assertEquals("guest-abc", entry.getOwner()); //test3
        assertEquals("Light Side", entry.getSeatId()); //test3
        assertEquals("Light Side", _server.hallServer.getPlayNowGames().seatFor(gameId, "guest-abc")); //test3
        assertEquals(null, _server.hallServer.getPlayNowGames().seatFor(gameId, "guest-other")); //test3
    }

    @Test
    public void PlayNowGuestGameRecordsNothing() throws Exception {
        //test1: no replay or game history is started for a guest game
        //test2: bot stats and achievements are never touched
        _server.hallServer.startPlayNowGame("guest-abc", null, PlayNowSide.DARK, PlayNowBot.OZZEL, "10.0.0.1");
        verify(_server.gameRecorder, never()).recordGame(any(), anyString(), anyString(), anyMap()); //test1
        verifyNoInteractions(_server.botStatsDAO); //test2
    }

    @Test
    public void PlayNowLoggedInPlayerPlaysUnderTheirNameAndItCounts() throws Exception {
        //test1: the seat is the username
        //test2: the game is recorded (replay and history) and bot stats are set up for the account
        Player bill = _server.member(42, "bill");
        String gameId = _server.hallServer.startPlayNowGame("bill", bill, PlayNowSide.DARK, PlayNowBot.OZZEL, "10.0.0.1");
        SwccgGameMediator game = _server.swccgoServer.getGameById(gameId);

        assertTrue(game.isPlayerPlaying("bill")); //test1
        assertEquals("bill", game.getGameState().getDarkPlayer()); //test1
        verify(_server.gameRecorder, times(1)).recordGame(eq(game), anyString(), anyString(), anyMap()); //test2
        verify(_server.botStatsDAO, atLeastOnce()).createPlayerStats(42); //test2
    }

    @Test
    public void PlayNowGamesAreListedInTheHallAndCanBeWatched() throws Exception {
        //test1: a logged-in player sees the guest game in the hall as a watchable game
        //test2: the row shows the guest's seat and the bot, and says it is a guest game
        //test3: spectators may chat in the game (to coach)
        String gameId = _server.hallServer.startPlayNowGame("guest-abc", null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, "10.0.0.1");
        HallInfoVisitor visitor = mock(HallInfoVisitor.class);
        _server.hallServer.processHall(_server.member(7, "watcher"), visitor);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SwccgGameParticipant>> players = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Boolean> watchable = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> desc = ArgumentCaptor.forClass(String.class);
        verify(visitor).visitTable(anyString(), eq(gameId), watchable.capture(), eq(HallInfoVisitor.TableStatus.PLAYING),
                anyString(), anyString(), anyString(), any(), anyString(), desc.capture(), players.capture(), anyMap(),
                eq(false), any(), anyBoolean(), any(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong());
        assertTrue(watchable.getValue()); //test1
        List<String> names = new ArrayList<>();
        players.getValue().forEach(p -> names.add(p.getPlayerId()));
        assertTrue(names.contains("Light Side")); //test2
        assertTrue(names.contains(Player.AI_BEGINNER_ID)); //test2
        assertEquals("Play now (guest)", desc.getValue()); //test2

        _server.swccgoServer.getGameChatRoom(gameId).joinUser("watcher", false, false);
        _server.swccgoServer.getGameChatRoom(gameId).sendMessage("watcher", "Deploy to the Cantina", false); //test3
    }

    @Test
    public void PlayNowRefusesNewGamesAtTheCap() throws Exception {
        //test1: with the cap at 1, a second Play now game is refused as busy
        //test2: a finished game no longer counts
        _server.hallServer.setMaxPlayNowGames(1);
        String first = _server.hallServer.startPlayNowGame("guest-a", null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, "10.0.0.1");
        assertThrows(HallServer.PlayNowBusyException.class,
                () -> _server.hallServer.startPlayNowGame("guest-b", null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, "10.0.0.2")); //test1

        _server.swccgoServer.getGameById(first).concede(PlayNowTestServer.seat("Light Side"));
        assertNotNull(_server.hallServer.startPlayNowGame("guest-b", null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, "10.0.0.2")); //test2
    }

    @Test
    public void PlayNowBotGamesHaveNoTimers() throws Exception {
        //test1: a bot game whose decision timeout and game clock have both run out is not ended
        //test2: the same timeouts end a game between two people (the timers are only off for bot games)
        String gameId = _server.hallServer.startPlayNowGame("guest-abc", null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, "10.0.0.1");
        SwccgGameMediator botGame = _server.swccgoServer.getGameById(gameId);
        expireTimers(botGame);
        botGame.cleanup();
        assertFalse(botGame.isFinished()); //test1

        Player light = _server.member(1, "light");
        Player dark = _server.member(2, "dark");
        SwccgGameMediator humans = _server.swccgoServer.createNewGame(botGame.getFormat(), null, "Casual",
                new SwccgGameParticipant[]{new SwccgGameParticipant("light", com.gempukku.swccgo.guest.GuestDecks.deck(PlayNowSide.LIGHT)),
                        new SwccgGameParticipant("dark", com.gempukku.swccgo.guest.GuestDecks.deck(PlayNowSide.DARK))},
                true, false, true, true, true, false, 300, 60, false, false, false, null, false);
        humans.startGame();
        expireTimers(humans);
        humans.cleanup();
        assertTrue(humans.isFinished()); //test2
    }

    private static void expireTimers(SwccgGameMediator game) throws Exception {
        set(game, "_playerDecisionTimeoutPeriod", 0);
        set(game, "_maxSecondsForGamePerPlayer", -1);
        @SuppressWarnings("unchecked")
        Map<String, Long> sent = (Map<String, Long>) ReflectionUtils.tryToReadFieldValue(SwccgGameMediator.class, "_decisionQuerySentTimes", game).get();
        sent.replaceAll((k, v) -> v - 10_000);
    }

    private static void set(Object target, String field, Object value) throws Exception {
        java.lang.reflect.Field f = SwccgGameMediator.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static AwaitingDecision pendingDecision(SwccgGameMediator game, String player) throws Exception {
        Object feedback = ReflectionUtils.tryToReadFieldValue(SwccgGameMediator.class, "_userFeedback", game).get();
        return ((com.gempukku.swccgo.logic.timing.DefaultUserFeedback) feedback).getAwaitingDecision(player);
    }
}
