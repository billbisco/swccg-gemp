package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The 2026 Retro League Prelude P-ANH precons, bundled with the server (deck XML as saved by the deck builder). Play
 * now always uses these, for guests and for logged-in players.
 */
public final class GuestDecks {
    private static final Pattern BLUEPRINT = Pattern.compile("blueprintId=\"([^\"]+)\"");

    private GuestDecks() {
    }

    /** A fresh copy of the precon for a side. */
    public static SwccgDeck deck(PlayNowSide side) {
        try (InputStream in = GuestDecks.class.getResourceAsStream(side.getDeckResource())) {
            if (in == null) {
                throw new IllegalStateException("Missing " + side.getDeckResource());
            }
            SwccgDeck deck = new SwccgDeck(side.getDeckName());
            Matcher m = BLUEPRINT.matcher(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            while (m.find()) {
                deck.addCard(m.group(1));
            }
            return deck;
        } catch (IOException e) {
            throw new IllegalStateException("Can't read " + side.getDeckResource(), e);
        }
    }
}
