package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** swccg-guild/work#90: deck size is chosen when setting up a table, league or tournament. */
public class DeckSizeHallTests {
    /** A guest deck cut down, or padded with extra copies of a card it already runs twice. */
    private static SwccgDeck deckOf(PlayNowSide side, int size) {
        SwccgDeck full = GuestDecks.deck(side);
        List<String> cards = new ArrayList<>(full.getCards());
        Map<String, Integer> counts = new HashMap<>();
        for (String card : cards)
            counts.merge(card, 1, Integer::sum);
        String repeat = counts.entrySet().stream().filter(e -> e.getValue() >= 2).findFirst().orElseThrow().getKey();
        while (cards.size() > size)
            cards.remove(cards.size() - 1);
        while (cards.size() < size)
            cards.add(repeat);
        SwccgDeck deck = new SwccgDeck(side.name().toLowerCase() + size);
        for (String card : cards)
            deck.addCard(card);
        for (String card : full.getCardsOutsideDeck())
            deck.addCardOutsideDeck(card);
        return deck;
    }

    private PlayNowTestServer server() throws Exception {
        var s = new PlayNowTestServer();
        s.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(s.swccgoServer);
        for (int size : new int[] {44, 45, 46, 60, 100}) {
            when(dao.getDeckForPlayer(any(Player.class), eq("light" + size))).thenReturn(deckOf(PlayNowSide.LIGHT, size));
            when(dao.getDeckForPlayer(any(Player.class), eq("dark" + size))).thenReturn(deckOf(PlayNowSide.DARK, size));
        }
        return s;
    }

    private static AwaitingTable table(PlayNowTestServer s, String format, Player host, String deck) throws HallException {
        return s.hallServer.createNewTable(format, host, deck, false, "deck size", false, null,
                false, null, null, false, "default", false, null);
    }

    private static String tableRowFormat(PlayNowTestServer s, Player viewer) {
        var visitor = mock(HallInfoVisitor.class);
        s.hallServer.processHall(viewer, visitor);
        var row = mockingDetails(visitor).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
        return row.getArgument(5) + "|" + row.getArgument(6) + "|" + row.getArgument(0) + "|" + row.getArgument(20);
    }

    @Test
    public void DeckSizeCasualTableAcceptsOnlyDecksOfTheChosenSize() throws Exception {
        var s = server();
        var host = s.member(1, "sizeHost");
        assertThrows("one under", HallException.class, () -> table(s, "open@45", host, "light44"));
        assertThrows("one over", HallException.class, () -> table(s, "open@45", host, "light46"));
        assertThrows("the format's own 60 no longer fits", HallException.class, () -> table(s, "open@45", host, "light60"));
        var table = table(s, "open@45", host, "light45");
        assertEquals(45, table.getSwccgoFormat().getRequiredDeckSize());
        assertEquals("open@45", table.getSwccgoFormat().getCode());

        // The joining player is held to the same size
        var opponent = s.member(2, "sizeOpponent");
        String[] row = tableRowFormat(s, opponent).split("\\|");
        assertThrows(HallException.class, () -> s.hallServer.joinTableAsPlayer(row[2], opponent, "dark60", false, null));
        assertTrue(s.hallServer.joinTableAsPlayer(row[2], opponent, "dark45", false, null));
    }

    private static AwaitingTable tableWithOptions(PlayNowTestServer s, String format, Player host, String deck,
            Map<String, String> options) throws HallException {
        return s.hallServer.createNewTable(format, host, deck, false, "options", false, null,
                false, null, null, false, false, options);
    }

    @Test
    public void GameSetupOptionsChosenAtSetupReachTheTableAndTheGame() throws Exception {
        var s = server();
        var host = s.member(1, "optionsHost");
        var options = new java.util.LinkedHashMap<String, String>();
        options.put("deckSize", "45");
        options.put("timer", "blitz");
        assertThrows("the deck size applies to the host's deck", HallException.class,
                () -> tableWithOptions(s, "open", host, "light60", options));
        var table = tableWithOptions(s, "open", host, "light45", options);
        assertEquals("open", table.getGameSetup().getFormatCode());
        assertEquals(45, table.getSwccgoFormat().getRequiredDeckSize());
        assertEquals(HallGameTimer.BLITZ, table.getGameTimer());
        var opponent = s.member(2, "optionsOpponent");
        String[] row = tableRowFormat(s, opponent).split("\\|");
        assertEquals("Open", row[0]);
        assertEquals("45 cards · Blitz", row[3]);
        assertThrows(HallException.class, () -> s.hallServer.joinTableAsPlayer(row[2], opponent, "dark60", false, null));
        assertTrue(s.hallServer.joinTableAsPlayer(row[2], opponent, "dark45", false, null));
        var game = s.swccgoServer.getGameById(table.getStartedGameId());
        assertEquals("45 cards · Blitz", game.getGameSetup().chipsText());
        assertTrue(game.getGameState().getLastMessages().contains("Game options: 45 cards · Blitz"));
    }

    @Test
    public void GameSetupRejectsBadOptionsAndLeftAloneMeansDefaults() throws Exception {
        var s = server();
        var host = s.member(1, "optionsHost");
        for (var bad : List.of(Map.of("deckSize", "101"), Map.of("startingCredits", "4"), Map.of("timer", "forever"), Map.of("noSuch", "1")))
            assertThrows(bad.toString(), HallException.class, () -> tableWithOptions(s, "open", host, "light60", bad));
        var table = tableWithOptions(s, "open", host, "light60", Map.of("deckSize", "", "timer", "default"));
        assertTrue(table.getGameSetup().isDefault());
        assertFalse(table.getSwccgoFormat().isDeckSizeChosen());
    }

    @Test
    public void DeckSizeOutsideTheSupportedRangeIsRefused() throws Exception {
        var s = server();
        var host = s.member(1, "sizeHost");
        for (String code : List.of("open@39", "open@101", "open@lots", "no_such_format@60"))
            assertThrows(code, HallException.class, () -> table(s, code, host, "light45"));
    }

    @Test
    public void DeckSizeLeftAloneIsTheFormatAsBefore() throws Exception {
        var s = server();
        var host = s.member(1, "sizeHost");
        var table = table(s, "open", host, "light60");
        assertFalse(table.getSwccgoFormat().isDeckSizeChosen());
        assertEquals("open", table.getSwccgoFormat().getCode());
        String[] row = tableRowFormat(s, s.member(2, "viewer")).split("\\|", -1);
        assertEquals("Open", row[0]);
        assertEquals("open", row[1]);
        assertEquals("no chips at the defaults", "", row[3]);
        assertTrue(table.getGameSetup().isDefault());
        assertThrows(HallException.class, () -> table(s, "open", s.member(3, "other"), "light45"));
    }

    @Test
    public void DeckSizeShowsInTheTableListAndTheGame() throws Exception {
        var s = server();
        var host = s.member(1, "sizeHost");
        var table = table(s, "open@100", host, "light100");
        var opponent = s.member(2, "sizeOpponent");
        String[] row = tableRowFormat(s, opponent).split("\\|");
        assertEquals("the format name never changes (#114)", "Open", row[0]);
        assertEquals("the deck-list key carries the size", "open@100", row[1]);
        assertEquals("100 cards", row[3]);
        assertTrue(s.hallServer.joinTableAsPlayer(row[2], opponent, "dark100", false, null));
        var game = s.swccgoServer.getGameById(table.getStartedGameId());
        assertEquals(100, game.getFormat().getRequiredDeckSize());
        assertEquals("100", game.getGameSetup().getChosen("deckSize"));
        assertTrue("the game log shows the options to players and spectators: " + game.getGameState().getLastMessages(),
                game.getGameState().getLastMessages().contains("Game options: 100 cards"));
    }

    @Test
    public void DeckSizeLeagueFixesTheSizeForAllItsGames() throws Exception {
        var s = server();
        var field = HallServer.class.getDeclaredField("_leagueService");
        field.setAccessible(true);
        var leagues = (LeagueService) field.get(s.hallServer);
        var league = mock(League.class);
        when(league.getType()).thenReturn("league45");
        when(league.getShowPlayerNames()).thenReturn(true);
        var serie = mock(LeagueSeriesData.class);
        when(serie.getFormat()).thenReturn("open@45");
        when(serie.getCollectionType()).thenReturn(CollectionType.ALL_CARDS);
        when(leagues.getLeagueByType("league45")).thenReturn(league);
        when(leagues.isPlayerInLeague(eq(league), any(Player.class))).thenReturn(true);
        when(leagues.getCurrentLeagueSeries(league)).thenReturn(serie);
        when(leagues.canPlayRankedGame(eq(league), eq(serie), anyString())).thenReturn(true);
        when(leagues.canPlayRankedGameAsSide(eq(league), eq(serie), anyString(), any())).thenReturn(true);
        when(leagues.canPlayRankedGameAgainst(eq(league), eq(serie), anyString(), anyString(), any())).thenReturn(true);

        var host = s.member(1, "leagueHost");
        assertThrows("a league game can't use another size", HallException.class, () -> table(s, "league45", host, "light60"));
        assertThrows("players can't change a league's options", HallException.class,
                () -> tableWithOptions(s, "league45", host, "light45", Map.of("deckSize", "60")));
        var table = table(s, "league45", host, "light45");
        assertEquals(45, table.getSwccgoFormat().getRequiredDeckSize());
        assertSame(league, table.getLeague());
        // The league type is the only thing a player sends; there is no way to ask for another size
        var opponent = s.member(2, "leagueOpponent");
        String[] row = tableRowFormat(s, opponent).split("\\|");
        assertEquals("Open", row[0]);
        assertEquals("45 cards", row[3]);
        assertThrows(HallException.class, () -> s.hallServer.joinTableAsPlayer(row[2], opponent, "dark60", false, null));
    }

    @Test
    public void DeckSizePlayerTournamentKeepsTheChosenSize() throws Exception {
        var s = server();
        var host = s.member(1, "tournamentHost");
        s.hallServer.createPlayerMadeQueue(host, "Size", "open@45", "swiss", 4, 4, 0, false,
                "light45", false, "dark45", false, null, null);
        var queues = HallServer.class.getDeclaredField("_tournamentQueues");
        queues.setAccessible(true);
        var all = (Map<?, ?>) queues.get(s.hallServer);
        var queue = all.values().stream().filter(q -> q instanceof com.gempukku.swccgo.tournament.PlayerMadeQueue)
                .map(q -> (com.gempukku.swccgo.tournament.PlayerMadeQueue) q).findFirst().orElseThrow();
        assertEquals("open@45", queue.getFormat());
        assertThrows(HallException.class, () -> s.hallServer.createPlayerMadeQueue(s.member(2, "otherHost"), "Size",
                "open@45", "swiss", 4, 4, 0, false, "light60", false, "dark60", false, null, null));
        assertThrows(HallException.class, () -> s.hallServer.createPlayerMadeQueue(s.member(3, "thirdHost"), "Size",
                "open@101", "swiss", 4, 4, 0, false, "light45", false, "dark45", false, null, null));
    }

    private static com.gempukku.swccgo.tournament.PlayerMadeQueue queueOf(PlayNowTestServer s, String host) throws Exception {
        var queues = HallServer.class.getDeclaredField("_tournamentQueues");
        queues.setAccessible(true);
        return ((Map<?, ?>) queues.get(s.hallServer)).values().stream()
                .filter(q -> q instanceof com.gempukku.swccgo.tournament.PlayerMadeQueue)
                .map(q -> (com.gempukku.swccgo.tournament.PlayerMadeQueue) q)
                .filter(q -> q.getHost().equals(host)).findFirst().orElseThrow();
    }

    @Test
    public void GameSetupPlayerTournamentTakesItsOptionsFromTheForm() throws Exception {
        var s = server();
        var library = s.hallServer.getClass().getDeclaredField("_formatLibrary");
        library.setAccessible(true);
        var formats = (com.gempukku.swccgo.game.formats.SwccgoFormatLibrary) library.get(s.hallServer);
        s.hallServer.createPlayerMadeQueue(s.member(1, "optHost"), "Opts", "open", "swiss", 4, 4, 0, false,
                "light100", false, "dark100", false, null, null, "constructed", null, null, 0, Map.of("deckSize", "100"));
        var queue = queueOf(s, "optHost");
        assertEquals("open@100", queue.getFormat());
        assertEquals("100 cards", queue.getGameSetup(formats).chipsText());
        // the hall lists the queue with its options
        var visitor = mock(HallInfoVisitor.class);
        s.hallServer.processHall(s.member(2, "viewer"), visitor);
        assertTrue(mockingDetails(visitor).getInvocations().stream()
                .anyMatch(i -> java.util.Arrays.asList(i.getArguments()).contains("100 cards")));
        // a casual-only option or one the format doesn't use is refused
        assertThrows(HallException.class, () -> s.hallServer.createPlayerMadeQueue(s.member(3, "timerHost"), "Opts", "open",
                "swiss", 4, 4, 0, false, "light60", false, "dark60", false, null, null, "constructed", null, null, 0,
                Map.of("timer", "blitz")));
        assertThrows(HallException.class, () -> s.hallServer.createPlayerMadeQueue(s.member(4, "creditsHost"), "Opts", "open",
                "swiss", 4, 4, 0, false, "light60", false, "dark60", false, null, null, "constructed", null, null, 0,
                Map.of("startingCredits", "4")));
        // no options is the format as it is
        s.hallServer.createPlayerMadeQueue(s.member(5, "plainHost"), "Opts", "open", "swiss", 4, 4, 0, false,
                "light60", false, "dark60", false, null, null, "constructed", null, null, 0, null);
        assertTrue(queueOf(s, "plainHost").getGameSetup(formats).isDefault());
    }

    @Test
    public void GameSetupTournamentGamesGetTheEventsSetup() throws Exception {
        var formats = com.gempukku.swccgo.framework.VirtualTableScenario._formatLibrary;
        var queue = new com.gempukku.swccgo.tournament.PlayerMadeQueue("q", "Q", "host", "open@45", "swiss", 2, 4, 0,
                false, null);
        var setup = com.gempukku.swccgo.game.setup.GameSetup.of(formats.getFormat("open"), Map.of("deckSize", "45"),
                com.gempukku.swccgo.game.setup.GameOption.Scope.TOURNAMENT);
        queue.setGameSetup(setup);
        assertSame(setup, queue.getGameSetup(formats));
        var tournament = com.gempukku.swccgo.tournament.PlayerConstructedTournament.limited("t", "T", "open@45", "swiss",
                2, false, List.of("a", "b"), CollectionType.ALL_CARDS,
                com.gempukku.swccgo.tournament.Tournament.Stage.PLAYING_GAMES, null);
        assertEquals("without a stored setup the code decides", "45 cards", tournament.getGameSetup(formats).chipsText());
        tournament.setGameSetup(setup.with("deckSize", "50"));
        assertEquals("50 cards", tournament.getGameSetup(formats).chipsText());
    }
}
