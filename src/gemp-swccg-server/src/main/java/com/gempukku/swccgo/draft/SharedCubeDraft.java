package com.gempukku.swccgo.draft;

import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import com.gempukku.swccgo.tournament.TournamentCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SharedCubeDraft implements Draft {
    public static final int PICK_TIME = 35 * 1000;
    public static final int CARDS_PER_PACK = 9;

    private final CollectionsManager _collectionsManager;
    private final CollectionType _collectionType;
    private final PackagedProductStorage _packStorage;
    private final List<String> _players;
    private final List<String> _lightPool;
    private final List<String> _darkPool;
    private final List<String> _wattoPacks;
    private final int _packsPerSide;
    private final boolean _watto;

    private final List<MutableCardCollection> _cardChoices = new ArrayList<MutableCardCollection>();
    private final Map<String, MutableCardCollection> _cardChoice = new HashMap<String, MutableCardCollection>();
    private final Map<String, DraftCommunicationChannel> _channels = new HashMap<String, DraftCommunicationChannel>();
    private final List<String> _exclusions = new ArrayList<String>();
    private int _nextChannelNumber;
    private int _nextPickNumber;
    private int _openedPacks;
    private int _totalPacks;
    private boolean _onDark;
    private boolean _finished;
    private long _lastPickStart;

    public SharedCubeDraft(CollectionsManager collectionsManager, CollectionType collectionType,
                           PackagedProductStorage packStorage, CardCollection fixedCollection,
                           List<String> lightPool, List<String> darkPool, List<String> wattoPacks,
                           int packsPerSide, Set<String> players) {
        _collectionsManager = collectionsManager;
        _collectionType = collectionType;
        _packStorage = packStorage;
        _lightPool = lightPool == null ? new ArrayList<String>() : new ArrayList<String>(lightPool);
        _darkPool = darkPool == null ? new ArrayList<String>() : new ArrayList<String>(darkPool);
        _wattoPacks = wattoPacks == null ? Collections.<String>emptyList() : wattoPacks;
        _watto = !_wattoPacks.isEmpty();
        _packsPerSide = Math.max(1, packsPerSide);
        _players = new ArrayList<String>(players);
        Collections.shuffle(_players);
        Collections.shuffle(_lightPool);
        Collections.shuffle(_darkPool);
        _totalPacks = _watto ? _wattoPacks.size() : (_packsPerSide * 2);
        CardCollection fixed = fixedCollection == null ? new DefaultCardCollection() : fixedCollection;
        for (String player : _players)
            _collectionsManager.addPlayerCollection(true, "New cube draft collection", player, _collectionType, fixed);
    }

    public static SharedCubeDraft jsonCube(CollectionsManager collectionsManager, CollectionType collectionType,
                                           CardCollection fixedCollection, List<String> lightPool,
                                           List<String> darkPool, int packsPerSide, Set<String> players) {
        return new SharedCubeDraft(collectionsManager, collectionType, null, fixedCollection, lightPool, darkPool,
                Collections.<String>emptyList(), packsPerSide, players);
    }

    public static SharedCubeDraft watto(CollectionsManager collectionsManager, CollectionType collectionType,
                                        PackagedProductStorage packStorage, CardCollection fixedCollection,
                                        List<String> packs, Set<String> players) {
        return new SharedCubeDraft(collectionsManager, collectionType, packStorage, fixedCollection,
                Collections.<String>emptyList(), Collections.<String>emptyList(), packs, 4, players);
    }

    @Override
    public void advanceDraft(TournamentCallback draftCallback) {
        if (haveAllPlayersPicked()) {
            if (haveAllCardsBeenChosen()) {
                if (_openedPacks < _totalPacks)
                    openNextPacks();
                else
                    _finished = true;
            } else {
                presentNewCardChoices();
            }
            for (DraftCommunicationChannel channel : _channels.values())
                channel.draftChanged();
        } else if (choiceTimePassed()) {
            forceRandomCardChoice();
            for (DraftCommunicationChannel channel : _channels.values())
                channel.draftChanged();
        }
    }

    @Override
    public void playerChosenCard(String playerName, String cardId) {
        playerChosen(playerName, cardId);
        DraftCommunicationChannel channel = _channels.get(playerName);
        if (channel != null)
            channel.draftChanged();
    }

    @Override
    public void signUpForDraft(String playerName, DraftChannelVisitor draftChannelVisitor) {
        DraftCommunicationChannel channel = new DraftCommunicationChannel(_nextChannelNumber++);
        _channels.put(playerName, channel);
        channel.processCommunicationChannel(getCardChoice(playerName), getChosenCards(playerName), draftChannelVisitor);
    }

    @Override
    public DraftCommunicationChannel getCommunicationChannel(String playerName, int channelNumber)
            throws SubscriptionExpiredException, SubscriptionConflictException {
        DraftCommunicationChannel channel = _channels.get(playerName);
        if (channel == null)
            throw new SubscriptionExpiredException();
        if (channel.getChannelNumber() != channelNumber)
            throw new SubscriptionConflictException();
        return channel;
    }

    @Override
    public DraftCardChoice getCardChoice(String playerName) {
        return new DefaultDraftCardChoice(_cardChoice.get(playerName), _lastPickStart + PICK_TIME);
    }

    @Override
    public CardCollection getChosenCards(String playerName) {
        return _collectionsManager.getPlayerCollection(playerName, _collectionType.getCode());
    }

    @Override
    public boolean isFinished() {
        return _finished;
    }

    private void forceRandomCardChoice() {
        Map<String, MutableCardCollection> copy = new HashMap<String, MutableCardCollection>(_cardChoice);
        for (Map.Entry<String, MutableCardCollection> entry : copy.entrySet()) {
            if (entry.getValue().getAll().isEmpty())
                continue;
            String cardId = entry.getValue().getAll().keySet().iterator().next();
            playerChosen(entry.getKey(), cardId);
        }
    }

    private void playerChosen(String playerName, String cardId) {
        MutableCardCollection choice = _cardChoice.get(playerName);
        if (choice != null && choice.removeItem(cardId, 1)) {
            _collectionsManager.addItemsToPlayerCollection(false, "Pick in draft", playerName, _collectionType,
                    Arrays.asList(CardCollection.Item.createItem(cardId, 1)));
            _cardChoice.remove(playerName);
        }
    }

    private void presentNewCardChoices() {
        for (int i = 0; i < _players.size(); i++)
            _cardChoice.put(_players.get(i), _cardChoices.get((i + _nextPickNumber) % _players.size()));
        _nextPickNumber++;
        _lastPickStart = System.currentTimeMillis();
    }

    private void openNextPacks() {
        _cardChoices.clear();
        int playerCount = _players.size();
        if (_watto) {
            String packId = _wattoPacks.get(_openedPacks);
            for (int i = 0; i < playerCount; i++) {
                MutableCardCollection pack = new DefaultCardCollection();
                List<CardCollection.Item> items = _packStorage.openPackagedProductWithExclusions(packId, _exclusions);
                for (CardCollection.Item item : items) {
                    pack.addItem(item.getBlueprintId(), item.getCount());
                    _exclusions.add(item.getBlueprintId());
                }
                _cardChoices.add(pack);
            }
        } else {
            List<String> pool = _onDark ? _darkPool : _lightPool;
            for (int i = 0; i < playerCount; i++) {
                MutableCardCollection pack = new DefaultCardCollection();
                for (int c = 0; c < CARDS_PER_PACK && !pool.isEmpty(); c++)
                    pack.addItem(pool.remove(0), 1);
                _cardChoices.add(pack);
            }
            _openedPacks++;
            if (!_onDark && _openedPacks >= _packsPerSide)
                _onDark = true;
            _nextPickNumber = 0;
            presentNewCardChoices();
            return;
        }
        _openedPacks++;
        _nextPickNumber = 0;
        presentNewCardChoices();
    }

    private boolean choiceTimePassed() {
        return System.currentTimeMillis() > PICK_TIME + _lastPickStart;
    }

    private boolean haveAllCardsBeenChosen() {
        return _cardChoices.isEmpty() || _cardChoices.get(0).getAll().isEmpty();
    }

    private boolean haveAllPlayersPicked() {
        return _cardChoice.isEmpty();
    }

}
