package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.draft.DefaultDraft;
import com.gempukku.swccgo.draft.Draft;
import com.gempukku.swccgo.draft.DraftPack;
import com.gempukku.swccgo.draft.SharedCubeDraft;
import com.gempukku.swccgo.draft.SoloBoosterDraft;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.league.SealedLeagueProduct;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerMadeQueue extends AbstractTournamentQueue implements TournamentQueue {
    public static final String PAIRING_SWISS = "swiss";
    public static final String PAIRING_MATCH_PLAY = "matchPlay";

    private final String _queueId;
    private final String _tournamentQueueName;
    private final String _host;
    private final String _pairing;
    private final int _totalGames;
    private final int _maxPlayers;
    private final int _readyCheckSeconds;
    private final boolean _privateEvent;
    private final long _createdAt;
    private final String _eventType;
    private final String _draftMode;
    private final TournamentProduct _product;
    private final int _packCount;
    private final SealedLeagueProduct _sealedProduct;
    private final PackagedProductStorage _packStorage;
    private final SoloDraftDefinitions _soloDraftDefinitions;
    private final TournamentCollectionRegistry _collectionRegistry;
    private final Map<String, SwccgDeck> _lightDecks = new LinkedHashMap<String, SwccgDeck>();
    private final Map<String, SwccgDeck> _darkDecks = new LinkedHashMap<String, SwccgDeck>();
    private final Set<String> _readyPlayers = new HashSet<String>();
    private boolean _cancelled;
    private boolean _started;
    private long _readyCheckDeadline;

    public PlayerMadeQueue(String queueId, String tournamentQueueName, String host, String format,
                           String pairing, int totalGames, int maxPlayers, int readyCheckSeconds,
                           boolean privateEvent, TournamentPrizes tournamentPrizes) {
        this(queueId, tournamentQueueName, host, format, pairing, totalGames, maxPlayers, readyCheckSeconds,
                privateEvent, tournamentPrizes, TournamentProduct.TYPE_CONSTRUCTED, null, null, 0,
                CollectionType.ALL_CARDS, null, null, null, null);
    }

    public PlayerMadeQueue(String queueId, String tournamentQueueName, String host, String format,
                           String pairing, int totalGames, int maxPlayers, int readyCheckSeconds,
                           boolean privateEvent, TournamentPrizes tournamentPrizes, String eventType,
                           String draftMode, TournamentProduct product, int packCount,
                           CollectionType collectionType, SealedLeagueProduct sealedProduct,
                           PackagedProductStorage packStorage, SoloDraftDefinitions soloDraftDefinitions,
                           TournamentCollectionRegistry collectionRegistry) {
        super(0, product == null || TournamentProduct.TYPE_CONSTRUCTED.equals(eventType),
                collectionType == null ? CollectionType.ALL_CARDS : collectionType, tournamentPrizes, null, format);
        _queueId = queueId;
        _tournamentQueueName = tournamentQueueName;
        _host = host;
        _pairing = pairing;
        _totalGames = ConstructedPairing.clampTotalGames(totalGames);
        _maxPlayers = ConstructedPairing.clampMaxPlayers(maxPlayers);
        _readyCheckSeconds = readyCheckSeconds;
        _privateEvent = privateEvent;
        _createdAt = System.currentTimeMillis();
        _eventType = eventType == null ? TournamentProduct.TYPE_CONSTRUCTED : eventType;
        _draftMode = draftMode;
        _product = product;
        _packCount = packCount;
        _sealedProduct = sealedProduct;
        _packStorage = packStorage;
        _soloDraftDefinitions = soloDraftDefinitions;
        _collectionRegistry = collectionRegistry;
    }

    public String getQueueId() {
        return _queueId;
    }

    public String getHost() {
        return _host;
    }

    public String getPairing() {
        return _pairing;
    }

    public int getTotalGames() {
        return _totalGames;
    }

    public Map<String, SwccgDeck> getLightDecks() {
        return new HashMap<String, SwccgDeck>(_lightDecks);
    }

    public Map<String, SwccgDeck> getDarkDecks() {
        return new HashMap<String, SwccgDeck>(_darkDecks);
    }

    public List<String> getSignedUpPlayers() {
        return new ArrayList<String>(_players);
    }

    @Override
    public String getPairingDescription() {
        return PAIRING_MATCH_PLAY.equals(_pairing) ? "Single Elimination Match Play" : "Swiss";
    }

    @Override
    public String getTournamentQueueName() {
        return _tournamentQueueName;
    }

    @Override
    public String getStartCondition() {
        if (_readyCheckDeadline > 0)
            return "Ready check";
        return "When " + _maxPlayers + " players join or when start is requested";
    }

    @Override
    public boolean isJoinable() {
        return !_cancelled && !_started && _players.size() < _maxPlayers && _readyCheckDeadline == 0;
    }

    @Override
    public boolean isPlayerMade() {
        return true;
    }

    @Override
    public boolean isHost(String player) {
        return _host.equals(player);
    }

    @Override
    public boolean isStartable(String player) {
        return isHost(player) && !_started && !_cancelled && _players.size() >= 2 && _readyCheckDeadline == 0;
    }

    @Override
    public boolean canCancel(String player) {
        return isHost(player) && !_started && !_cancelled;
    }

    @Override
    public String getSignedUpPlayersCsv() {
        return StringUtils.join(_players, ", ");
    }

    @Override
    public int getMaxPlayers() {
        return _maxPlayers;
    }

    @Override
    public int getReadyCheckSeconds() {
        return _readyCheckSeconds;
    }

    @Override
    public boolean isPrivateEvent() {
        return _privateEvent;
    }

    @Override
    public long getCreatedAt() {
        return _createdAt;
    }

    public String getEventType() {
        return _eventType;
    }

    public String getDraftMode() {
        return _draftMode;
    }

    public String getProductCode() {
        return _product == null ? "" : _product.getCode();
    }

    public boolean isLimited() {
        return TournamentProduct.isLimitedType(_eventType);
    }

    @Override
    public synchronized void joinPlayer(CollectionsManager collectionsManager, Player player, SwccgDeck lightDeck, SwccgDeck darkDeck) {
        if (!isJoinable())
            return;
        if (_players.contains(player.getName()))
            return;
        if (!isLimited() && (lightDeck == null || darkDeck == null))
            return;
        _players.add(player.getName());
        if (lightDeck != null)
            _lightDecks.put(player.getName(), lightDeck);
        if (darkDeck != null)
            _darkDecks.put(player.getName(), darkDeck);
        if (lightDeck != null)
            _playerDecks.put(player.getName(), lightDeck);
        if (_collectionRegistry != null && getCollectionType() != null)
            _collectionRegistry.addPlayer(getCollectionType().getCode(), player.getName());
    }

    @Override
    public synchronized void leavePlayer(CollectionsManager collectionsManager, Player player) {
        if (_started)
            return;
        if (player.getName().equals(_host)) {
            _cancelled = true;
            _players.clear();
            _lightDecks.clear();
            _darkDecks.clear();
            _playerDecks.clear();
            _readyPlayers.clear();
            return;
        }
        super.leavePlayer(collectionsManager, player);
        _lightDecks.remove(player.getName());
        _darkDecks.remove(player.getName());
        _readyPlayers.remove(player.getName());
    }

    @Override
    public synchronized void requestStart(String player) {
        if (!isStartable(player))
            return;
        if (_readyCheckSeconds > 0) {
            _readyCheckDeadline = System.currentTimeMillis() + (_readyCheckSeconds * 1000L);
            _readyPlayers.clear();
        } else {
            _started = true;
        }
    }

    @Override
    public synchronized void confirmReady(String player) {
        if (_readyCheckDeadline <= 0 || _started || _cancelled)
            return;
        if (_players.contains(player))
            _readyPlayers.add(player);
        if (_readyPlayers.containsAll(_players))
            _started = true;
    }

    @Override
    public synchronized void cancel(String player) {
        if (canCancel(player))
            _cancelled = true;
    }

    @Override
    public synchronized boolean isReadyCheckActive() {
        return _readyCheckDeadline > 0 && !_started && !_cancelled;
    }

    @Override
    public synchronized int getReadyCheckSecsRemaining() {
        if (!isReadyCheckActive())
            return -1;
        long remaining = _readyCheckDeadline - System.currentTimeMillis();
        return (int) Math.max(0, remaining / 1000L);
    }

    @Override
    public synchronized boolean hasConfirmedReady(String player) {
        return player != null && _readyPlayers.contains(player);
    }

    @Override
    public synchronized boolean process(TournamentQueueCallback tournamentQueueCallback, CollectionsManager collectionsManager) {
        if (_cancelled) {
            leaveAllPlayers(collectionsManager);
            return true;
        }
        if (_started) {
            return startNow(tournamentQueueCallback, collectionsManager);
        }
        if (_readyCheckDeadline > 0 && System.currentTimeMillis() >= _readyCheckDeadline) {
            List<String> drop = new ArrayList<String>();
            for (String player : _players) {
                if (!_readyPlayers.contains(player))
                    drop.add(player);
            }
            for (String player : drop) {
                _players.remove(player);
                _lightDecks.remove(player);
                _darkDecks.remove(player);
                _playerDecks.remove(player);
            }
            _readyCheckDeadline = 0;
            if (_players.size() >= 2) {
                _started = true;
                return startNow(tournamentQueueCallback, collectionsManager);
            }
            _cancelled = true;
            leaveAllPlayers(collectionsManager);
            return true;
        }
        if (!_started && _players.size() >= _maxPlayers && _readyCheckDeadline == 0) {
            if (_readyCheckSeconds > 0) {
                _readyCheckDeadline = System.currentTimeMillis() + (_readyCheckSeconds * 1000L);
                _readyPlayers.clear();
            } else {
                _started = true;
                return startNow(tournamentQueueCallback, collectionsManager);
            }
        }
        return false;
    }

    private boolean startNow(TournamentQueueCallback tournamentQueueCallback, CollectionsManager collectionsManager) {
        if (_players.size() < 2) {
            _started = false;
            _readyCheckDeadline = 0;
            return false;
        }
        PlayerConstructedTournament tournament;
        if (isLimited() && _product != null)
            tournament = startLimited(collectionsManager);
        else
            tournament = new PlayerConstructedTournament(
                    _queueId, _tournamentQueueName, _format, _pairing, _totalGames, _privateEvent,
                    new ArrayList<String>(_players), _lightDecks, _darkDecks);
        tournamentQueueCallback.createTournament(tournament);
        return true;
    }

    private PlayerConstructedTournament startLimited(CollectionsManager collectionsManager) {
        CollectionType collectionType = getCollectionType();
        List<String> players = new ArrayList<String>(_players);
        if (_collectionRegistry != null)
            _collectionRegistry.register(collectionType, players,
                    _product.isJsonCube() ? _product.getCubeDraftType() : null);

        if (TournamentProduct.TYPE_SEALED.equals(_eventType)) {
            CardCollection kit = _product.sealedKit(_sealedProduct);
            for (String player : players)
                collectionsManager.addPlayerCollection(true, "Sealed tournament product", player, collectionType, kit);
            return PlayerConstructedTournament.limited(_queueId, _tournamentQueueName, _format, _pairing, _totalGames,
                    _privateEvent, players, collectionType, PlayerConstructedTournament.Stage.DECK_BUILDING, null);
        }

        CardCollection keep = _product.draftKeep(_sealedProduct);
        if (_product.isJsonCube()) {
            CubeDraftPools pools = CubeDraftPools.load(_product.getCubeDraftType());
            keep = pools.startingKeep();
            if (TournamentProduct.MODE_SOLO.equals(_draftMode)) {
                issueCubeSolo(collectionsManager, collectionType, players, keep);
                return PlayerConstructedTournament.limited(_queueId, _tournamentQueueName, _format, _pairing, _totalGames,
                        _privateEvent, players, collectionType, PlayerConstructedTournament.Stage.DRAFT, null)
                        .cubeSolo(_product.getCubeDraftType());
            }
            int packs = _packCount > 0 ? _packCount : 6;
            Draft draft = SharedCubeDraft.jsonCube(collectionsManager, collectionType, keep,
                    pools.lightCards, pools.darkCards, packs, new HashSet<String>(players));
            return PlayerConstructedTournament.limited(_queueId, _tournamentQueueName, _format, _pairing, _totalGames,
                    _privateEvent, players, collectionType, PlayerConstructedTournament.Stage.DRAFT, draft);
        }

        List<String> packs = _product.packsForCount(_packCount);
        DraftPack draftPack = new DraftPack(keep, packs);
        Draft draft;
        if (TournamentProduct.MODE_SOLO.equals(_draftMode))
            draft = new SoloBoosterDraft(collectionsManager, collectionType, _packStorage, draftPack, new HashSet<String>(players));
        else if (_product.isWattoCube())
            draft = SharedCubeDraft.watto(collectionsManager, collectionType, _packStorage, keep, packs, new HashSet<String>(players));
        else
            draft = new DefaultDraft(collectionsManager, collectionType, _packStorage, draftPack, new HashSet<String>(players));
        return PlayerConstructedTournament.limited(_queueId, _tournamentQueueName, _format, _pairing, _totalGames,
                _privateEvent, players, collectionType, PlayerConstructedTournament.Stage.DRAFT, draft);
    }

    private void issueCubeSolo(CollectionsManager collectionsManager, CollectionType collectionType,
                               List<String> players, CardCollection keep) {
        SoloDraft soloDraft = _soloDraftDefinitions == null ? null : _soloDraftDefinitions.getSoloDraft(_product.getCubeDraftType());
        for (String player : players) {
            long seed = System.nanoTime() ^ player.hashCode();
            CardCollection starting = keep;
            if (soloDraft != null && soloDraft.initializeNewCollection(seed) != null) {
                DefaultCardCollection merged = new DefaultCardCollection(keep);
                for (Map.Entry<String, CardCollection.Item> item : soloDraft.initializeNewCollection(seed).getAll().entrySet())
                    merged.addItem(item.getKey(), item.getValue().getCount());
                starting = merged;
            }
            DefaultCardCollection collection = new DefaultCardCollection(starting);
            Map<String, Object> extra = new HashMap<String, Object>();
            extra.put("seed", seed);
            extra.put("stage", 0);
            extra.put("stageCount", soloDraft == null ? 0 : soloDraft.stageCount());
            extra.put("finished", Boolean.FALSE);
            extra.put("soloDraftType", _product.getCubeDraftType());
            collection.setExtraInformation(extra);
            collectionsManager.addPlayerCollection(true, "Cube solo draft", player, collectionType, collection);
        }
    }

    public static Side requiredSide(SwccgDeck deck, com.gempukku.swccgo.game.SwccgCardBlueprintLibrary library, Side expected) {
        if (deck == null)
            return null;
        Side side = deck.getSide(library);
        return side == expected ? side : null;
    }
}
