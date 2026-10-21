package com.gempukku.swccgo.guest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Play now games in memory: who owns each one and which seat is theirs. The owner is a guest id for a guest and
 * the username for a logged-in player.
 */
public class PlayNowGames {

    /** One Play now game. */
    public static final class Entry {
        private final String _gameId;
        private volatile String _owner;
        private final String _seatId;
        private final PlayNowSide _side;
        private final PlayNowBot _bot;
        private final String _ip;
        private volatile boolean _startedAsGuest;
        private volatile boolean _chatMuted;

        public Entry(String gameId, String owner, String seatId, PlayNowSide side, PlayNowBot bot, String ip, boolean startedAsGuest) {
            _gameId = gameId;
            _owner = owner;
            _seatId = seatId;
            _side = side;
            _bot = bot;
            _ip = ip;
            _startedAsGuest = startedAsGuest;
        }

        public String getGameId() {
            return _gameId;
        }

        public String getOwner() {
            return _owner;
        }

        /** The player's participant id in the game ("Light Side"/"Dark Side" for a guest, else the username). */
        public String getSeatId() {
            return _seatId;
        }

        public PlayNowSide getSide() {
            return _side;
        }

        public PlayNowBot getBot() {
            return _bot;
        }

        /** IP that started it (in memory only, for the per-IP limit). */
        public String getIp() {
            return _ip;
        }

        /** Whether the owner is (still) a guest. */
        public boolean isGuest() {
            return GuestIdentity.isGuestId(_owner);
        }

        /** Started by a guest who has since signed up; the result goes on their account. */
        public boolean isStartedAsGuest() {
            return _startedAsGuest;
        }

        public boolean isChatMuted() {
            return _chatMuted;
        }

        public void setChatMuted(boolean muted) {
            _chatMuted = muted;
        }

        void adopt(String account) {
            _owner = account;
            _startedAsGuest = true;
        }
    }

    private final Map<String, Entry> _games = new ConcurrentHashMap<>();

    public void add(Entry entry) {
        _games.put(entry.getGameId(), entry);
    }

    public Entry get(String gameId) {
        return gameId == null ? null : _games.get(gameId);
    }

    public void remove(String gameId) {
        _games.remove(gameId);
    }

    public Collection<Entry> all() {
        return new ArrayList<>(_games.values());
    }

    public List<Entry> ownedBy(String owner) {
        List<Entry> result = new ArrayList<>();
        for (Entry e : _games.values()) {
            if (e.getOwner().equals(owner)) {
                result.add(e);
            }
        }
        return result;
    }

    /** Live guest games started from one IP. */
    public int countGuestGamesFromIp(String ip) {
        int n = 0;
        for (Entry e : _games.values()) {
            if (e.isGuest() && ip != null && ip.equals(e.getIp())) {
                n++;
            }
        }
        return n;
    }

    /**
     * A guest signed up: their games now belong to the account, and their results will count on it.
     * @return the games moved
     */
    public List<Entry> adopt(String guestId, String account) {
        List<Entry> moved = ownedBy(guestId);
        for (Entry e : moved) {
            e.adopt(account);
        }
        return moved;
    }

    /**
     * The seat a player may act as in a game: their own seat if they own it, else null.
     */
    public String seatFor(String gameId, String owner) {
        Entry e = get(gameId);
        return e != null && e.getOwner().equals(owner) ? e.getSeatId() : null;
    }
}
