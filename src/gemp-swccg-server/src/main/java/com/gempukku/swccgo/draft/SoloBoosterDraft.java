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
import com.gempukku.swccgo.tournament.TournamentProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SoloBoosterDraft implements Draft {
    private final CollectionsManager _collectionsManager;
    private final CollectionType _collectionType;
    private final PackagedProductStorage _packagedProductStorage;
    private final DraftPack _draftPack;
    private final List<String> _humans;
    private final int _seatCount;
    private final Random _random = new Random();

    private final List<MutableCardCollection> _seatPacks = new ArrayList<MutableCardCollection>();
    private final Map<String, MutableCardCollection> _humanChoice = new HashMap<String, MutableCardCollection>();
    private final Map<String, DraftCommunicationChannel> _channels = new HashMap<String, DraftCommunicationChannel>();
    private int _nextChannelNumber;
    private int _nextPackIndex;
    private int _passOffset;
    private boolean _finished;
    private long _lastPickStart;

    public SoloBoosterDraft(CollectionsManager collectionsManager, CollectionType collectionType,
                            PackagedProductStorage packagedProductStorage, DraftPack draftPack, Set<String> players) {
        _collectionsManager = collectionsManager;
        _collectionType = collectionType;
        _packagedProductStorage = packagedProductStorage;
        _draftPack = draftPack;
        _humans = new ArrayList<String>(players);
        Collections.shuffle(_humans);
        _seatCount = Math.max(TournamentProduct.SOLO_TABLE_SEATS, _humans.size());
        CardCollection fixed = _draftPack.getFixedCollection();
        for (String player : _humans)
            _collectionsManager.addPlayerCollection(true, "New draft fixed collection", player, _collectionType, fixed);
    }

    @Override
    public void advanceDraft(TournamentCallback draftCallback) {
        if (_finished)
            return;
        if (!haveAllHumansPicked())
            return;
        aiPickAndRotate();
        if (currentPacksEmpty()) {
            if (_nextPackIndex < _draftPack.getPacks().size())
                openNextPacks();
            else
                _finished = true;
        } else {
            presentHumanChoices();
        }
        for (DraftCommunicationChannel channel : _channels.values())
            channel.draftChanged();
    }

    @Override
    public void playerChosenCard(String playerName, String cardId) {
        MutableCardCollection choice = _humanChoice.get(playerName);
        if (choice != null && choice.removeItem(cardId, 1)) {
            _collectionsManager.addItemsToPlayerCollection(false, "Pick in draft", playerName, _collectionType,
                    Arrays.asList(CardCollection.Item.createItem(cardId, 1)));
            _humanChoice.remove(playerName);
            DraftCommunicationChannel channel = _channels.get(playerName);
            if (channel != null)
                channel.draftChanged();
        }
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
        return new DefaultDraftCardChoice(_humanChoice.get(playerName), _lastPickStart + 24 * 60 * 60 * 1000L);
    }

    @Override
    public CardCollection getChosenCards(String playerName) {
        return _collectionsManager.getPlayerCollection(playerName, _collectionType.getCode());
    }

    @Override
    public boolean isFinished() {
        return _finished;
    }

    private void openNextPacks() {
        _seatPacks.clear();
        String packId = _draftPack.getPacks().get(_nextPackIndex);
        for (int i = 0; i < _seatCount; i++) {
            MutableCardCollection pack = new DefaultCardCollection();
            pack.addItem(packId, 1);
            pack.openPack(packId, null, _packagedProductStorage);
            _seatPacks.add(pack);
        }
        _nextPackIndex++;
        _passOffset = 0;
        presentHumanChoices();
    }

    private void presentHumanChoices() {
        _humanChoice.clear();
        for (int i = 0; i < _humans.size(); i++) {
            MutableCardCollection pack = _seatPacks.get(seatIndex(i));
            if (pack != null && pack.getAll().size() > 0)
                _humanChoice.put(_humans.get(i), pack);
        }
        _lastPickStart = System.currentTimeMillis();
    }

    private void aiPickAndRotate() {
        for (int seat = 0; seat < _seatCount; seat++) {
            if (seat < _humans.size())
                continue;
            MutableCardCollection pack = _seatPacks.get(seatIndex(seat));
            if (pack == null || pack.getAll().isEmpty())
                continue;
            List<String> ids = new ArrayList<String>(pack.getAll().keySet());
            String pick = ids.get(_random.nextInt(ids.size()));
            pack.removeItem(pick, 1);
        }
        _passOffset++;
    }

    private int seatIndex(int seat) {
        return (seat + _passOffset) % _seatCount;
    }

    private boolean haveAllHumansPicked() {
        if (_seatPacks.isEmpty() && _nextPackIndex == 0)
            return true;
        return _humanChoice.isEmpty();
    }

    private boolean currentPacksEmpty() {
        if (_seatPacks.isEmpty())
            return true;
        for (MutableCardCollection pack : _seatPacks) {
            if (pack.getAll().size() > 0)
                return false;
        }
        return true;
    }
}
