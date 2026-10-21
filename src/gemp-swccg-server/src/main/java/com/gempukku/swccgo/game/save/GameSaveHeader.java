package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

/**
 * Everything needed to rebuild a game from scratch: format, decks, seed and who plays. Stored as JSON.
 */
public final class GameSaveHeader {
    public static final int VERSION = 1;

    /** One seat. {@code bot} is the bot name for a bot seat, null for a person. */
    public static final class Seat {
        public String playerId;
        public String displayName;
        public String bot;
        public String deckName;
        public List<String> cards = new ArrayList<>();
        public List<String> cardsOutsideDeck = new ArrayList<>();
        public String deckHash;

        public SwccgDeck toDeck() {
            SwccgDeck deck = new SwccgDeck(deckName);
            cards.forEach(deck::addCard);
            cardsOutsideDeck.forEach(deck::addCardOutsideDeck);
            return deck;
        }
    }

    public int version = VERSION;
    public String gameId;
    public String formatCode;
    public long seed;
    public List<Seat> seats = new ArrayList<>();
    /** Build that started the game. */
    public String buildCommit;
    public long createdAt;
    public long updatedAt;
    /** Turns played so far (both players), as of the last save. Shown next to Resume. */
    public int turn;
    /** Fingerprint of the Kryo class list the snapshot was written with; a snapshot is only read with the same list. */
    public String kryoClassList;

    private static final Gson GSON = new Gson();

    public static Seat seat(String playerId, String displayName, String bot, SwccgDeck deck) {
        Seat s = new Seat();
        s.playerId = playerId;
        s.displayName = displayName;
        s.bot = bot;
        s.deckName = deck.getDeckName();
        s.cards.addAll(deck.getCards());
        s.cardsOutsideDeck.addAll(deck.getCardsOutsideDeck());
        s.deckHash = deckHash(deck);
        return s;
    }

    /** SHA-256 (first 16 hex) of the deck's cards and outside-deck cards, in order. */
    public static String deckHash(SwccgDeck deck) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(String.join(",", deck.getCards()).getBytes(StandardCharsets.UTF_8));
            md.update((byte) '|');
            md.update(String.join(",", deck.getCardsOutsideDeck()).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(md.digest(), 0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public Seat seat(String playerId) {
        for (Seat s : seats) {
            if (s.playerId.equals(playerId)) {
                return s;
            }
        }
        return null;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static GameSaveHeader fromJson(String json) {
        GameSaveHeader h = GSON.fromJson(json, GameSaveHeader.class);
        if (h.version != VERSION) {
            throw new IllegalArgumentException("Unknown save header version " + h.version);
        }
        for (Seat s : h.seats) {
            if (!deckHash(s.toDeck()).equals(s.deckHash)) {
                throw new IllegalArgumentException("Deck hash mismatch for " + s.playerId);
            }
        }
        return h;
    }
}
