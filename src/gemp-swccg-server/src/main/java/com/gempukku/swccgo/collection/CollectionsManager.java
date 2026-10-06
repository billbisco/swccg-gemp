package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.common.CardCounts;
import com.gempukku.swccgo.db.CollectionDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CollectionsManager {
    private static Logger _logger = LogManager.getLogger(CollectionsManager.class);
    private final CollectionLocks _locks = new CollectionLocks();

    private PlayerDAO _playerDAO;
    private CollectionDAO _collectionDAO;
    private TransferDAO _transferDAO;

    private CountDownLatch _collectionReadyLatch = new CountDownLatch(1);
    private DefaultCardCollection _defaultCollection;
    private DefaultCardCollection _defaultCollectionWithPlaytesting;

    public CollectionsManager(PlayerDAO playerDAO, CollectionDAO collectionDAO, TransferDAO transferDAO, final SwccgCardBlueprintLibrary library) {
        _playerDAO = playerDAO;
        _collectionDAO = collectionDAO;
        _transferDAO = transferDAO;

        _defaultCollection = new DefaultCardCollection();
        _defaultCollectionWithPlaytesting = new DefaultCardCollection();

        // Add cards to default collection
        addCardsToDefaultCollection(library, CardCounts.FULL_SETS_CARD_COUNTS, 1, _defaultCollection);
        addCardsToDefaultCollection(library, CardCounts.PREMIUM_SETS_CARD_COUNTS, 101, _defaultCollection);
        addCardsToDefaultCollection(library, CardCounts.VIRTUAL_SETS_CARD_COUNTS, 200, _defaultCollection);
        addCardsToDefaultCollection(library, CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS, 301, _defaultCollection);
        addCardsToDefaultCollection(library, CardCounts.DREAM_CARD_SETS_CARD_COUNTS, 401, _defaultCollection);
        addCardsToDefaultCollection(library, CardCounts.LEGACY_SETS_CARD_COUNTS, 601, _defaultCollection);

        // Add cards to default collection with playtesting
        addCardsToDefaultCollection(library, CardCounts.FULL_SETS_CARD_COUNTS, 1, _defaultCollectionWithPlaytesting);
        addCardsToDefaultCollection(library, CardCounts.PREMIUM_SETS_CARD_COUNTS, 101, _defaultCollectionWithPlaytesting);
        addCardsToDefaultCollection(library, CardCounts.VIRTUAL_SETS_CARD_COUNTS, 200, _defaultCollectionWithPlaytesting);
        addCardsToDefaultCollection(library, CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS, 301, _defaultCollectionWithPlaytesting);
        addCardsToDefaultCollection(library, CardCounts.DREAM_CARD_SETS_CARD_COUNTS, 401, _defaultCollectionWithPlaytesting);
        addCardsToDefaultCollection(library, CardCounts.PLAYTESTING_SETS_CARD_COUNTS, 501, _defaultCollectionWithPlaytesting);
        addCardsToDefaultCollection(library, CardCounts.LEGACY_SETS_CARD_COUNTS, 601, _defaultCollectionWithPlaytesting);

        _collectionReadyLatch.countDown();
    }

    /**
     * Test constructor: empty default collections, no card-library load.
     */
    CollectionsManager(PlayerDAO playerDAO, CollectionDAO collectionDAO, TransferDAO transferDAO) {
        _playerDAO = playerDAO;
        _collectionDAO = collectionDAO;
        _transferDAO = transferDAO;
        _defaultCollection = new DefaultCardCollection();
        _defaultCollectionWithPlaytesting = new DefaultCardCollection();
        _collectionReadyLatch.countDown();
    }

    /**
     * Adds all the cards to the default collection.
     * @param library the card blueprint library
     * @param cardSetCounts the counts of cards in each set
     * @param setIndexOffset the set number of the first array item in cardSetCounts
     * @param defaultCollection the default collection
     */
    private void addCardsToDefaultCollection(SwccgCardBlueprintLibrary library, int[] cardSetCounts, int setIndexOffset, DefaultCardCollection defaultCollection) {
        for (int i = 0; i < cardSetCounts.length; i++) {
            int setNum = setIndexOffset + i;
            _logger.debug("Loading set " + setNum);
            for (int j = 1; j <= cardSetCounts[i]; j++) {
                String blueprintId = setNum + "_" + j;
                try {
                    if (library.getBaseBlueprintId(blueprintId).equals(blueprintId)) {
                        final SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
                        if (blueprint != null) {
                            defaultCollection.addItem(blueprintId, 60);
                        }
                    }
                } catch (IllegalArgumentException exp) {
                }
            }
        }
    }

    public CardCollection getDefaultCollection(boolean withPlaytesting) {
        try {
            _collectionReadyLatch.await();
        } catch (InterruptedException exp) {
            throw new RuntimeException("Error while awaiting loading a default collection", exp);
        }
        return withPlaytesting ? _defaultCollectionWithPlaytesting : _defaultCollection;
    }

    public CardCollection getPlayerCollection(String playerName, String collectionType) {
        return getPlayerCollection(_playerDAO.getPlayer(playerName), collectionType);
    }

    public CardCollection getPlayerCollection(Player player, String collectionType) {
        if (collectionType.contains("+"))
            return createSumCollection(player, collectionType.split("\\+"));

        if ("default".equals(collectionType)) {
            boolean withPlaytesting = player.hasType(Player.Type.ADMIN) || player.hasType(Player.Type.PLAYTESTER);
            return getDefaultCollection(withPlaytesting);
        }

        ReentrantReadWriteLock lock = _locks.forCollection(player.getId(), collectionType);
        lock.readLock().lock();
        try {
            final CardCollection collection = _collectionDAO.getPlayerCollection(player.getId(), collectionType);

            if (collection == null && "permanent".equals(collectionType)) {
                return new DefaultCardCollection();
            }

            return collection;
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player collection", exp);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to get player collection", exp);
        } finally {
            lock.readLock().unlock();
        }
    }

    private CardCollection createSumCollection(Player player, String[] collectionTypes) {
        List<CardCollection> collections = new LinkedList<CardCollection>();
        for (String collectionType : collectionTypes)
            collections.add(getPlayerCollection(player, collectionType));

        return new SumCardCollection(collections);
    }

    private void setPlayerCollection(Player player, String collectionType, CardCollection cardCollection) {
        if (collectionType.contains("+"))
            throw new IllegalArgumentException("Invalid collection type: " + collectionType);
        try {
            _collectionDAO.setPlayerCollection(player.getId(), collectionType, cardCollection);
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to store player collection", exp);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to store player collection", exp);
        }
    }

    public void addPlayerCollection(boolean notifyPlayer, String reason, String player, CollectionType collectionType, CardCollection cardCollection) {
        addPlayerCollection(notifyPlayer, reason, _playerDAO.getPlayer(player), collectionType, cardCollection);
    }

    public void addPlayerCollection(boolean notifyPlayer, String reason, Player player, CollectionType collectionType, CardCollection cardCollection) {
        if (collectionType.getCode().contains("+"))
            throw new IllegalArgumentException("Invalid collection type: " + collectionType);

        writeCollection(player, collectionType.getCode(), () -> {
            persist(player, collectionType, cardCollection, () ->
                    _transferDAO.addTransferTo(notifyPlayer, player.getName(), reason, collectionType.getFullName(), cardCollection.getCurrency(), cardCollection));
            return null;
        });
    }

    public Map<Player, CardCollection> getPlayersCollection(String collectionType) {
        if (collectionType.contains("+"))
            throw new IllegalArgumentException("Invalid collection type: " + collectionType);

        try {
            final Map<Integer, CardCollection> playerCollectionsByType = _collectionDAO.getPlayerCollectionsByType(collectionType);

            Map<Player, CardCollection> result = new HashMap<Player, CardCollection>();
            for (Map.Entry<Integer, CardCollection> playerCollection : playerCollectionsByType.entrySet())
                result.put(_playerDAO.getPlayer(playerCollection.getKey()), playerCollection.getValue());

            return result;
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get players collection", exp);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to get players collection", exp);
        }
    }

    public CardCollection openPackInPlayerCollection(Player player, CollectionType collectionType, String selection, PackagedProductStorage packagedProductStorage, String packId) {
        return writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection == null)
                return null;
            MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);

            final CardCollection packContents = mutableCardCollection.openPack(packId, selection, packagedProductStorage);
            if (packContents != null) {
                persist(player, collectionType, mutableCardCollection, () -> {
                    String reason = "Opened pack";
                    _transferDAO.addTransferFrom(player.getName(), reason, collectionType.getFullName(), 0, cardCollectionFromBlueprintId(1, packId));
                    _transferDAO.addTransferTo(true, player.getName(), reason, collectionType.getFullName(), packContents.getCurrency(), packContents);
                });
            }
            return packContents;
        });
    }

    /**
     * Opens auto-openable packs in one write lock: in-memory loop, one save,
     * one transfer-from, one transfer-to (which notifies). Cap is card-yielding packs.
     */
    public OpenAllPacks.Result openAllOpenablePacks(Player player, CollectionType collectionType,
                                                    PackagedProductStorage packagedProductStorage) {
        return writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection == null)
                return null;
            MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
            Map<String, Integer> before = itemCounts(mutableCardCollection);
            int currencyBefore = mutableCardCollection.getCurrency();

            OpenAllPacks.Result result = OpenAllPacks.open(mutableCardCollection, packagedProductStorage, OpenAllPacks.DEFAULT_CAP);

            Map<String, Integer> after = itemCounts(mutableCardCollection);
            int currencyAfter = mutableCardCollection.getCurrency();
            DefaultCardCollection consumed = decreasedItems(before, after);
            DefaultCardCollection produced = increasedItems(before, after);
            int currencyFrom = Math.max(0, currencyBefore - currencyAfter);
            int currencyTo = Math.max(0, currencyAfter - currencyBefore);

            if (result.opened > 0 || !consumed.getAll().isEmpty() || !produced.getAll().isEmpty()
                    || currencyFrom > 0 || currencyTo > 0) {
                persist(player, collectionType, mutableCardCollection, () -> {
                    String reason = "Opened pack";
                    _transferDAO.addTransferFrom(player.getName(), reason, collectionType.getFullName(), currencyFrom, consumed);
                    _transferDAO.addTransferTo(true, player.getName(), reason, collectionType.getFullName(), currencyTo, produced);
                });
            }
            return result;
        });
    }

    private static Map<String, Integer> itemCounts(CardCollection collection) {
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        for (CardCollection.Item item : collection.getAll().values())
            counts.put(item.getBlueprintId(), item.getCount());
        return counts;
    }

    private static DefaultCardCollection decreasedItems(Map<String, Integer> before, Map<String, Integer> after) {
        DefaultCardCollection result = new DefaultCardCollection();
        for (Map.Entry<String, Integer> entry : before.entrySet()) {
            Integer next = after.get(entry.getKey());
            int remaining = next != null ? next.intValue() : 0;
            int delta = entry.getValue() - remaining;
            if (delta > 0)
                result.addItem(entry.getKey(), delta);
        }
        return result;
    }

    private static DefaultCardCollection increasedItems(Map<String, Integer> before, Map<String, Integer> after) {
        DefaultCardCollection result = new DefaultCardCollection();
        for (Map.Entry<String, Integer> entry : after.entrySet()) {
            Integer prev = before.get(entry.getKey());
            int previous = prev != null ? prev.intValue() : 0;
            int delta = entry.getValue() - previous;
            if (delta > 0)
                result.addItem(entry.getKey(), delta);
        }
        return result;
    }

    private CardCollection cardCollectionFromBlueprintId(int count, String blueprintId) {
        DefaultCardCollection result = new DefaultCardCollection();
        result.addItem(blueprintId, count);
        return result;
    }

    public void addItemsToPlayerCollection(boolean notifyPlayer, String reason, Player player, CollectionType collectionType, Collection<CardCollection.Item> items, Map<String, Object> extraInformation) {
        writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection != null) {
                MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
                MutableCardCollection addedCards = new DefaultCardCollection();
                for (CardCollection.Item item : items) {
                    mutableCardCollection.addItem(item.getBlueprintId(), item.getCount());
                    addedCards.addItem(item.getBlueprintId(), item.getCount());
                }

                if (extraInformation != null) {
                    Map<String, Object> resultExtraInformation = new HashMap<String, Object>(playerCollection.getExtraInformation());
                    resultExtraInformation.putAll(extraInformation);
                    mutableCardCollection.setExtraInformation(resultExtraInformation);
                }

                persist(player, collectionType, mutableCardCollection, () ->
                        _transferDAO.addTransferTo(notifyPlayer, player.getName(), reason, collectionType.getFullName(), 0, addedCards));
            }
            return null;
        });
    }

    public void addItemsToPlayerCollection(boolean notifyPlayer, String reason, Player player, CollectionType collectionType, Collection<CardCollection.Item> items) {
        addItemsToPlayerCollection(notifyPlayer, reason, player, collectionType, items, null);
    }

    public void addItemsToPlayerCollection(boolean notifyPlayer, String reason, String player, CollectionType collectionType, Collection<CardCollection.Item> items) {
        addItemsToPlayerCollection(notifyPlayer, reason, _playerDAO.getPlayer(player), collectionType, items);
    }

    public boolean tradeCards(Player player, CollectionType collectionType, String removeBlueprintId, int removeCount, String addBlueprintId, int addCount, int currencyCost) {
        return writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection != null) {
                MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
                if (!mutableCardCollection.removeItem(removeBlueprintId, removeCount))
                    return false;
                if (!mutableCardCollection.removeCurrency(currencyCost))
                    return false;
                mutableCardCollection.addItem(addBlueprintId, addCount);

                persist(player, collectionType, mutableCardCollection, () -> {
                    String reason = "Trading items";
                    _transferDAO.addTransferFrom(player.getName(), reason, collectionType.getFullName(), currencyCost, cardCollectionFromBlueprintId(removeCount, removeBlueprintId));
                    _transferDAO.addTransferTo(true, player.getName(), reason, collectionType.getFullName(), 0, cardCollectionFromBlueprintId(addCount, addBlueprintId));
                });
                return true;
            }
            return false;
        });
    }

    public boolean buyCardToPlayerCollection(Player player, CollectionType collectionType, String blueprintId, int currency) {
        return writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection != null) {
                MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
                if (!mutableCardCollection.removeCurrency(currency))
                    return false;
                mutableCardCollection.addItem(blueprintId, 1);

                persist(player, collectionType, mutableCardCollection, () -> {
                    String reason = "Items bought";
                    _transferDAO.addTransferFrom(player.getName(), reason, collectionType.getFullName(), currency, new DefaultCardCollection());
                    _transferDAO.addTransferTo(true, player.getName(), reason, collectionType.getFullName(), 0, cardCollectionFromBlueprintId(1, blueprintId));
                });
                return true;
            }
            return false;
        });
    }

    public boolean sellCardInPlayerCollection(Player player, CollectionType collectionType, String blueprintId, int currency) {
        return writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection != null) {
                MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
                if (!mutableCardCollection.removeItem(blueprintId, 1))
                    return false;
                mutableCardCollection.addCurrency(currency);

                persist(player, collectionType, mutableCardCollection, () -> {
                    _transferDAO.addTransferFrom(player.getName(), "Selling items", collectionType.getFullName(), 0, cardCollectionFromBlueprintId(1, blueprintId));
                    _transferDAO.addTransferTo(false, player.getName(), "Selling items", collectionType.getFullName(), currency, new DefaultCardCollection());
                });
                return true;
            }
            return false;
        });
    }

    public boolean sellAllOfACardInPlayerCollection(Player player, CollectionType collectionType, String blueprintId, int currency) {
        return writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection != null) {
                MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
                int itemCount = mutableCardCollection.getItemCount(blueprintId);
                if (!mutableCardCollection.removeItem(blueprintId, itemCount))
                    return false;
                mutableCardCollection.addCurrency(currency*itemCount);

                persist(player, collectionType, mutableCardCollection, () -> {
                    _transferDAO.addTransferFrom(player.getName(), "Selling items", collectionType.getFullName(), 0, cardCollectionFromBlueprintId(itemCount, blueprintId));
                    _transferDAO.addTransferTo(false, player.getName(), "Selling items", collectionType.getFullName(), currency*itemCount, new DefaultCardCollection());
                });
                return true;
            }
            return false;
        });
    }

    public void addCurrencyToPlayerCollection(boolean notifyPlayer, String reason, String player, CollectionType collectionType, int currency) {
        addCurrencyToPlayerCollection(notifyPlayer, reason, _playerDAO.getPlayer(player), collectionType, currency);
    }

    public void addCurrencyToPlayerCollection(boolean notifyPlayer, String reason, Player player, CollectionType collectionType, int currency) {
        if (currency > 0) {
            writeCollection(player, collectionType.getCode(), () -> {
                final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
                if (playerCollection != null) {
                    MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
                    mutableCardCollection.addCurrency(currency);

                    persist(player, collectionType, mutableCardCollection, () ->
                            _transferDAO.addTransferTo(notifyPlayer, player.getName(), reason, collectionType.getFullName(), currency, new DefaultCardCollection()));
                }
                return null;
            });
        }
    }

    public boolean removeCurrencyFromPlayerCollection(String reason, Player player, CollectionType collectionType, int currency) {
        return writeCollection(player, collectionType.getCode(), () -> {
            final CardCollection playerCollection = getPlayerCollection(player, collectionType.getCode());
            if (playerCollection != null) {
                MutableCardCollection mutableCardCollection = new DefaultCardCollection(playerCollection);
                if (mutableCardCollection.removeCurrency(currency)) {
                    persist(player, collectionType, mutableCardCollection, () ->
                            _transferDAO.addTransferFrom(player.getName(), reason, collectionType.getFullName(), currency, new DefaultCardCollection()));
                    return true;
                }
            }
            return false;
        });
    }

    private interface WriteAction<T> {
        T run();
    }

    private <T> T writeCollection(Player player, String collectionType, WriteAction<T> action) {
        ReentrantReadWriteLock lock = _locks.forCollection(player.getId(), collectionType);
        lock.writeLock().lock();
        try {
            return action.run();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void persist(Player player, CollectionType collectionType, CardCollection collection, Runnable transfers) {
        _collectionDAO.runInTransaction(() -> {
            setPlayerCollection(player, collectionType.getCode(), collection);
            transfers.run();
        });
    }
}
