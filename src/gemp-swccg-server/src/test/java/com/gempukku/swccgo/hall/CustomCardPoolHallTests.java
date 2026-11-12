package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.setup.CardPool;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.gempukku.swccgo.tournament.PlayerConstructedTournament;
import com.gempukku.swccgo.tournament.PlayerMadeQueue;
import com.gempukku.swccgo.tournament.Tournament;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.game.formats.CustomCardPoolTestSupport.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/** swccg-guild/work#222: a player-made tournament with a custom card pool, end to end through the hall. */
public class CustomCardPoolHallTests {
    private static final SwccgoFormatLibrary FORMATS = VirtualTableScenario._formatLibrary;

    private final SwccgDeck _light = GuestDecks.deck(PlayNowSide.LIGHT);
    private final SwccgDeck _dark = GuestDecks.deck(PlayNowSide.DARK);

    private PlayNowTestServer server(Map<String, SwccgDeck> decks) throws Exception {
        var s = new PlayNowTestServer();
        s.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(s.swccgoServer);
        for (Map.Entry<String, SwccgDeck> deck : decks.entrySet())
            when(dao.getDeckForPlayer(any(Player.class), eq(deck.getKey()))).thenReturn(deck.getValue());
        return s;
    }

    private static PlayerMadeQueue queueOf(PlayNowTestServer s, String host) throws Exception {
        var queues = HallServer.class.getDeclaredField("_tournamentQueues");
        queues.setAccessible(true);
        return ((Map<?, ?>) queues.get(s.hallServer)).values().stream()
                .filter(q -> q instanceof PlayerMadeQueue).map(q -> (PlayerMadeQueue) q)
                .filter(q -> q.getHost().equals(host)).findFirst().orElse(null);
    }

    private static void create(PlayNowTestServer s, Player host, String light, String dark, CardPool pool) throws HallException {
        s.hallServer.createPlayerMadeQueue(host, "Pool", "open", "swiss", 4, 4, 0, false, light, false, dark, false,
                null, null, "constructed", null, null, 0, null, pool);
    }

    @Test
    public void ACustomPoolEventChecksEveryDeckAgainstItsOwnSnapshot() throws Exception {
        SwccgFormat open = FORMATS.getFormat("open");
        List<Integer> sets = setsOf(_light, _dark);
        int excluded = setsOf(_light).get(setsOf(_light).size() - 1);
        List<Integer> playing = new ArrayList<>(sets);
        playing.remove(Integer.valueOf(excluded));
        List<String> allowed = new ArrayList<>(cardsFromSet(_light, excluded));
        allowed.addAll(cardsFromSet(_dark, excluded));
        int included = playing.get(0);
        String banned = unusedLightCard(_light, included, open);
        CardPool pool = CardPool.of(playing, allowed, List.of(banned));

        SwccgDeck bannedDeck = withCard(_light, banned);
        SwccgDeck foilDeck = withCard(_light, banned + "*");
        var s = server(Map.of("light", _light, "dark", _dark, "lightBanned", bannedDeck, "lightFoil", foilDeck));

        // AC1: the preview (what the form shows) and the saved event agree
        GameSetup preview = GameSetup.of(open, Map.of(), GameOption.Scope.TOURNAMENT).withPool(pool);
        create(s, s.member(1, "poolHost"), "light", "dark", pool);
        PlayerMadeQueue queue = queueOf(s, "poolHost");
        assertNotNull(queue);
        assertEquals(preview, queue.getGameSetup(FORMATS));
        List<String> summary = s.hallServer.getEventRulesSummary(queue.getQueueId());
        assertEquals("Format: Open", summary.get(0));
        assertEquals("Options: Custom card pool", summary.get(1));
        assertEquals(FORMATS.describePool(preview), summary.subList(2, summary.size()));
        assertTrue(summary.stream().anyMatch(l -> l.startsWith("Banned: ") && l.contains(banned + ")")));

        // AC2: joiners are held to the pool; the banned card is named, its foil too
        HallException refused = assertThrows(HallException.class, () -> s.hallServer.joinPlayerMadeQueue(queue.getQueueId(),
                s.member(2, "bannedPlayer"), "lightBanned", false, "dark", false, null, null));
        assertTrue(refused.getMessage(), refused.getMessage().contains(banned));
        assertThrows(HallException.class, () -> s.hallServer.joinPlayerMadeQueue(queue.getQueueId(),
                s.member(3, "foilPlayer"), "lightFoil", false, "dark", false, null, null));
        assertTrue(s.hallServer.joinPlayerMadeQueue(queue.getQueueId(), s.member(4, "legalPlayer"), "light", false, "dark", false, null, null));

        // AC3: the event is locked once open; a different setup is refused
        assertThrows(IllegalStateException.class, () -> queue.setGameSetup(preview.withPool(CardPool.NONE)));
        queue.setGameSetup(preview); // the same snapshot is not a change
        // a reload of the stored setup resolves the same rules
        GameSetup reloaded = GameSetup.fromJson(queue.getGameSetup(FORMATS).toJson(), "open");
        assertEquals(preview, reloaded);
        assertThrows(DeckInvalidException.class, () -> FORMATS.effectiveFormat(reloaded).validateDeck(bannedDeck));
        FORMATS.effectiveFormat(reloaded).validateDeck(_light);
        // the tournament the queue becomes carries the same snapshot to its rounds and matches
        PlayerConstructedTournament tournament = PlayerConstructedTournament.limited("t", "T", "open", "swiss", 2, false,
                List.of("poolHost", "legalPlayer"), CollectionType.ALL_CARDS, Tournament.Stage.PLAYING_GAMES, null);
        tournament.setGameSetup(queue.getGameSetup(FORMATS));
        assertEquals(pool, tournament.getGameSetup(FORMATS).getPool());

        // AC4: a second event on Open without a custom pool keeps Open's own rules
        create(s, s.member(5, "plainHost"), "lightBanned", "dark", CardPool.NONE);
        assertTrue(queueOf(s, "plainHost").getGameSetup(FORMATS).isDefault());
        assertEquals(List.of("Format: Open"), s.hallServer.getEventRulesSummary(queueOf(s, "plainHost").getQueueId()));
    }

    @Test
    public void AnInvalidPoolCreatesNothing() throws Exception {
        var s = server(Map.of("light", _light, "dark", _dark));
        HallException contradiction = assertThrows(HallException.class, () -> create(s, s.member(1, "badHost"), "light", "dark",
                CardPool.of(null, List.of("1_80"), List.of("1_80"))));
        assertTrue(contradiction.getMessage(), contradiction.getMessage().contains("both allowed and banned"));
        assertNull("nothing is created when an exception is invalid", queueOf(s, "badHost"));
        assertThrows(HallException.class, () -> create(s, s.member(2, "unknownHost"), "light", "dark",
                CardPool.of(List.of(9999), null, null)));
        assertNull(queueOf(s, "unknownHost"));
        // the host's own decks must fit the pool
        List<Integer> one = List.of(setsOf(_light).get(0));
        assertThrows(HallException.class, () -> create(s, s.member(3, "narrowHost"), "light", "dark", CardPool.of(one, null, null)));
        assertNull(queueOf(s, "narrowHost"));
        // limited events have their product's pool
        assertThrows(HallException.class, () -> s.hallServer.createPlayerMadeQueue(s.member(4, "sealedHost"), "Pool", "open",
                "swiss", 4, 4, 0, false, null, false, null, false, null, null, "sealed", null, null, 0, null,
                CardPool.of(null, null, List.of("1_80"))));
        assertNull(s.hallServer.getEventRulesSummary("no-such-event"));
    }
}
