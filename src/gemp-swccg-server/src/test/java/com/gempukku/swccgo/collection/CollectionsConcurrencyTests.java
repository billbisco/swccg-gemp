package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.db.CollectionDAO;
import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.Player;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CollectionsConcurrencyTests {

    @Test
    public void SameKeyUsesSameStripe() {
        CollectionLocks locks = new CollectionLocks();
        ReentrantReadWriteLock a = locks.forCollection(10, "permanent");
        ReentrantReadWriteLock b = locks.forCollection(10, "permanent");
        assertSame(a, b);
    }

    @Test
    public void StripeCountIsBounded() {
        assertEquals(64, CollectionLocks.STRIPE_COUNT);
        CollectionLocks locks = new CollectionLocks();
        Map<ReentrantReadWriteLock, Integer> seen = new HashMap<ReentrantReadWriteLock, Integer>();
        for (int i = 0; i < 1000; i++) {
            ReentrantReadWriteLock lock = locks.forCollection(i, "permanent");
            Integer n = seen.get(lock);
            seen.put(lock, n == null ? 1 : n + 1);
        }
        assertTrue(seen.size() <= CollectionLocks.STRIPE_COUNT);
        assertTrue(seen.size() > 1);
    }

    @Test
    public void DifferentPlayersCanAddGoldAtTheSameTime() throws Exception {
        InMemoryCollectionDAO collections = new InMemoryCollectionDAO();
        InMemoryTransferDAO transfers = new InMemoryTransferDAO();
        FakePlayerDAO players = new FakePlayerDAO();
        Player a = players.add(1, "G43810");
        Player b = players.add(2, "G43811");
        CollectionsManager manager = new CollectionsManager(players, collections, transfers);

        CyclicBarrier start = new CyclicBarrier(2);
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger errors = new AtomicInteger();
        Thread ta = new Thread(() -> {
            try {
                start.await(5, TimeUnit.SECONDS);
                manager.addCurrencyToPlayerCollection(false, "test", a, CollectionType.MY_CARDS, 7);
            } catch (Exception e) {
                errors.incrementAndGet();
            } finally {
                done.countDown();
            }
        });
        Thread tb = new Thread(() -> {
            try {
                start.await(5, TimeUnit.SECONDS);
                manager.addCurrencyToPlayerCollection(false, "test", b, CollectionType.MY_CARDS, 11);
            } catch (Exception e) {
                errors.incrementAndGet();
            } finally {
                done.countDown();
            }
        });
        ta.start();
        tb.start();
        assertTrue(done.await(10, TimeUnit.SECONDS));
        assertEquals(0, errors.get());
        assertEquals(7, manager.getPlayerCollection(a, "permanent").getCurrency());
        assertEquals(11, manager.getPlayerCollection(b, "permanent").getCurrency());
    }

    @Test
    public void SamePlayerGoldAddsSerialize() throws Exception {
        InMemoryCollectionDAO collections = new InMemoryCollectionDAO();
        InMemoryTransferDAO transfers = new InMemoryTransferDAO();
        FakePlayerDAO players = new FakePlayerDAO();
        Player a = players.add(1, "G43810");
        CollectionsManager manager = new CollectionsManager(players, collections, transfers);

        int threads = 20;
        CyclicBarrier start = new CyclicBarrier(threads);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger errors = new AtomicInteger();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await(5, TimeUnit.SECONDS);
                    manager.addCurrencyToPlayerCollection(false, "test", a, CollectionType.MY_CARDS, 1);
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        assertTrue(done.await(15, TimeUnit.SECONDS));
        assertEquals(0, errors.get());
        assertEquals(threads, manager.getPlayerCollection(a, "permanent").getCurrency());
    }

    @Test
    public void FirstTimeInsertsLeaveOneRow() throws Exception {
        InMemoryCollectionDAO collections = new InMemoryCollectionDAO();
        InMemoryTransferDAO transfers = new InMemoryTransferDAO();
        FakePlayerDAO players = new FakePlayerDAO();
        Player a = players.add(3, "NewPlayer");
        CollectionsManager manager = new CollectionsManager(players, collections, transfers);

        int threads = 8;
        CyclicBarrier start = new CyclicBarrier(threads);
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await(5, TimeUnit.SECONDS);
                    manager.addCurrencyToPlayerCollection(false, "signup", a, CollectionType.MY_CARDS, 5);
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            }).start();
        }
        assertTrue(done.await(15, TimeUnit.SECONDS));
        assertEquals(1, collections.rowCount(3, "permanent"));
        assertEquals(threads * 5, manager.getPlayerCollection(a, "permanent").getCurrency());
    }

    @Test
    public void WritersCopySoCacheIsNotAliased() throws Exception {
        InMemoryCollectionDAO inner = new InMemoryCollectionDAO();
        CachedCollectionDAO cached = new CachedCollectionDAO(inner);
        InMemoryTransferDAO transfers = new InMemoryTransferDAO();
        FakePlayerDAO players = new FakePlayerDAO();
        Player a = players.add(1, "G43810");
        CollectionsManager manager = new CollectionsManager(players, cached, transfers);
        manager.addCurrencyToPlayerCollection(false, "seed", a, CollectionType.MY_CARDS, 1);

        CardCollection first = cached.getPlayerCollection(1, "permanent");
        manager.addCurrencyToPlayerCollection(false, "more", a, CollectionType.MY_CARDS, 4);
        CardCollection second = cached.getPlayerCollection(1, "permanent");
        assertNotSame(first, second);
        assertEquals(5, second.getCurrency());
        assertEquals(1, first.getCurrency());
    }

    @Test
    public void PrizeLoopUnlocksBetweenPlayers() throws Exception {
        InMemoryCollectionDAO collections = new InMemoryCollectionDAO();
        InMemoryTransferDAO transfers = new InMemoryTransferDAO();
        FakePlayerDAO players = new FakePlayerDAO();
        Player a = players.add(1, "G43810");
        Player b = players.add(2, "G43811");
        CollectionsManager manager = new CollectionsManager(players, collections, transfers);
        DefaultCardCollection prize = new DefaultCardCollection();
        prize.addItem("Death Star II Booster Box", 1);

        manager.addItemsToPlayerCollection(true, "prize", a, CollectionType.MY_CARDS, prize.getAll().values());
        manager.addItemsToPlayerCollection(true, "prize", b, CollectionType.MY_CARDS, prize.getAll().values());

        assertEquals(1, manager.getPlayerCollection(a, "permanent").getItemCount("Death Star II Booster Box"));
        assertEquals(1, manager.getPlayerCollection(b, "permanent").getItemCount("Death Star II Booster Box"));
    }

    @Test
    public void ConsumeIsPerPlayer() throws Exception {
        InMemoryTransferDAO transfers = new InMemoryTransferDAO();
        DefaultCardCollection pack = new DefaultCardCollection();
        pack.addItem("1_1", 1);
        transfers.addTransferTo(true, "G43810", "Opened pack", "My cards", 0, pack);
        transfers.addTransferTo(true, "G43811", "Opened pack", "My cards", 0, pack);

        CyclicBarrier start = new CyclicBarrier(2);
        CountDownLatch done = new CountDownLatch(2);
        Map<String, Integer> sizes = new ConcurrentHashMap<String, Integer>();
        Thread ta = new Thread(() -> {
            try {
                start.await(5, TimeUnit.SECONDS);
                sizes.put("a", transfers.consumeUndeliveredPackages("G43810").size());
            } catch (Exception ignored) {
            } finally {
                done.countDown();
            }
        });
        Thread tb = new Thread(() -> {
            try {
                start.await(5, TimeUnit.SECONDS);
                sizes.put("b", transfers.consumeUndeliveredPackages("G43811").size());
            } catch (Exception ignored) {
            } finally {
                done.countDown();
            }
        });
        ta.start();
        tb.start();
        assertTrue(done.await(10, TimeUnit.SECONDS));
        assertEquals(Integer.valueOf(1), sizes.get("a"));
        assertEquals(Integer.valueOf(1), sizes.get("b"));
        assertTrue(transfers.consumeUndeliveredPackages("G43810").isEmpty());
        assertTrue(transfers.consumeUndeliveredPackages("G43811").isEmpty());
    }

    @Test
    public void SamePlayerSecondConsumeIsEmpty() {
        InMemoryTransferDAO transfers = new InMemoryTransferDAO();
        DefaultCardCollection pack = new DefaultCardCollection();
        pack.addItem("1_1", 1);
        transfers.addTransferTo(true, "G43810", "Opened pack", "My cards", 0, pack);
        assertEquals(1, transfers.consumeUndeliveredPackages("G43810").size());
        assertTrue(transfers.consumeUndeliveredPackages("G43810").isEmpty());
    }

    private static final class InMemoryCollectionDAO implements CollectionDAO {
        private final Map<String, CardCollection> _rows = new ConcurrentHashMap<String, CardCollection>();

        private String key(int playerId, String type) {
            return playerId + "-" + type;
        }

        public int rowCount(int playerId, String type) {
            return _rows.containsKey(key(playerId, type)) ? 1 : 0;
        }

        @Override
        public Map<Integer, CardCollection> getPlayerCollectionsByType(String type) {
            Map<Integer, CardCollection> result = new HashMap<Integer, CardCollection>();
            for (Map.Entry<String, CardCollection> entry : _rows.entrySet()) {
                String[] parts = entry.getKey().split("-", 2);
                if (parts.length == 2 && parts[1].equals(type))
                    result.put(Integer.parseInt(parts[0]), entry.getValue());
            }
            return result;
        }

        @Override
        public CardCollection getPlayerCollection(int playerId, String type) {
            return _rows.get(key(playerId, type));
        }

        @Override
        public void setPlayerCollection(int playerId, String type, CardCollection collection) {
            _rows.put(key(playerId, type), collection);
        }
    }

    private static final class InMemoryTransferDAO implements TransferDAO {
        private static final class Row {
            final String player;
            final String name;
            final int currency;
            final CardCollection items;
            boolean notify;
            Row(String player, String name, int currency, CardCollection items, boolean notify) {
                this.player = player;
                this.name = name;
                this.currency = currency;
                this.items = items;
                this.notify = notify;
            }
        }

        private final List<Row> _rows = Collections.synchronizedList(new ArrayList<Row>());
        private final ConcurrentHashMap<String, Object> _playerLocks = new ConcurrentHashMap<String, Object>();

        private Object lockFor(String player) {
            Object created = new Object();
            Object existing = _playerLocks.putIfAbsent(player, created);
            return existing != null ? existing : created;
        }

        @Override
        public boolean hasUndeliveredPackages(String player) {
            synchronized (lockFor(player)) {
                for (Row row : _rows) {
                    if (row.notify && row.player.equals(player))
                        return true;
                }
                return false;
            }
        }

        @Override
        public Map<String, ? extends CardCollection> consumeUndeliveredPackages(String player) {
            synchronized (lockFor(player)) {
                Map<String, DefaultCardCollection> result = new HashMap<String, DefaultCardCollection>();
                for (Row row : _rows) {
                    if (row.notify && row.player.equals(player)) {
                        DefaultCardCollection cardCollection = result.get(row.name);
                        if (cardCollection == null)
                            cardCollection = new DefaultCardCollection();
                        cardCollection.addCurrency(row.currency);
                        for (CardCollection.Item item : row.items.getAll().values())
                            cardCollection.addItem(item.getBlueprintId(), item.getCount());
                        result.put(row.name, cardCollection);
                        row.notify = false;
                    }
                }
                return result;
            }
        }

        @Override
        public void addTransferTo(boolean notifyPlayer, String player, String reason, String collectionName, int currency, CardCollection items) {
            _rows.add(new Row(player, collectionName, currency, items, notifyPlayer));
        }

        @Override
        public void addTransferFrom(String player, String reason, String collectionName, int currency, CardCollection items) {
            _rows.add(new Row(player, collectionName, currency, items, false));
        }
    }

    private static final class FakePlayerDAO implements PlayerDAO {
        private final Map<Integer, Player> _byId = new HashMap<Integer, Player>();
        private final Map<String, Player> _byName = new HashMap<String, Player>();

        Player add(int id, String name) {
            Player player = new Player(id, name, "x", "u", null, null, null, null);
            _byId.put(id, player);
            _byName.put(name, player);
            return player;
        }

        @Override
        public Player getPlayer(int id) {
            return _byId.get(id);
        }

        @Override
        public Player getPlayer(String playerName) {
            return _byName.get(playerName);
        }

        @Override
        public Player getPlayer(String playerName, boolean includeDeactivated) {
            return getPlayer(playerName);
        }

        @Override
        public boolean registerPlayer(String playerName, String password, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException {
            return false;
        }

        @Override
        public Player loginPlayer(String playerName, String password) {
            return null;
        }

        @Override
        public boolean updateLastLoginIp(String playerName, String remoteAddr) {
            return false;
        }

        @Override
        public boolean updateLastReward(Player player, Integer previousReward, int currentReward) {
            return false;
        }

        @Override
        public boolean resetUserPassword(String playerName) {
            return false;
        }

        @Override
        public boolean setPlayerFlag(String playerName, Player.Type flag, boolean status) {
            return false;
        }

        @Override
        public List<Player> findPlayersWithFlag(Player.Type flag) {
            return Collections.emptyList();
        }

        @Override
        public List<String> findPlayerNamesByPrefix(String prefix, int limit) {
            return Collections.emptyList();
        }

        @Override
        public boolean banPlayerPermanently(String playerName) {
            return false;
        }

        @Override
        public boolean banPlayerTemporarily(String playerName, long dateTo) {
            return false;
        }

        @Override
        public boolean unBanPlayer(String playerName) {
            return false;
        }

        @Override
        public List<Player> findSimilarAccounts(String playerName) {
            return Collections.emptyList();
        }
    }
}
