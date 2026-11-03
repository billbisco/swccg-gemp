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
        return row.getArgument(5) + "|" + row.getArgument(6) + "|" + row.getArgument(0);
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
        assertEquals("Open|open", tableRowFormat(s, s.member(2, "viewer")).replaceAll("\\|[^|]*$", ""));
        assertThrows(HallException.class, () -> table(s, "open", s.member(3, "other"), "light45"));
    }

    @Test
    public void DeckSizeShowsInTheTableListAndTheGame() throws Exception {
        var s = server();
        var host = s.member(1, "sizeHost");
        var table = table(s, "open@100", host, "light100");
        var opponent = s.member(2, "sizeOpponent");
        String[] row = tableRowFormat(s, opponent).split("\\|");
        assertEquals("Open (100 cards)", row[0]);
        assertEquals("open@100", row[1]);
        assertTrue(s.hallServer.joinTableAsPlayer(row[2], opponent, "dark100", false, null));
        var game = s.swccgoServer.getGameById(table.getStartedGameId());
        assertEquals("open@100", game.getFormat().getCode());
        assertTrue("the game log shows the chosen size to players and spectators: " + game.getGameState().getLastMessages(),
                game.getGameState().getLastMessages().contains("Deck size: 100 cards (chosen at setup)"));
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
        var table = table(s, "league45", host, "light45");
        assertEquals(45, table.getSwccgoFormat().getRequiredDeckSize());
        assertSame(league, table.getLeague());
        // The league type is the only thing a player sends; there is no way to ask for another size
        var opponent = s.member(2, "leagueOpponent");
        String[] row = tableRowFormat(s, opponent).split("\\|");
        assertEquals("Open (45 cards)", row[0]);
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
}
