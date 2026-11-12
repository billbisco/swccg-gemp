package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Save rules for the deck editor opened from the join screen (work#221).
 * <ul>
 *     <li>A deck's revision is a short hash of its stored contents, sent with the deck so an editor can say which
 *     version it started from.</li>
 *     <li>"Save as new deck" never replaces an existing deck: the save is refused when the name is taken.</li>
 *     <li>"Save changes" with the revision it started from is refused when the stored deck has changed since (or was
 *     deleted or renamed), instead of silently replacing the newer save.</li>
 * </ul>
 * Saves without these parameters (the standalone Deckbuilder) behave as before.
 */
final class DeckSaveGuard {
    static final int CONFLICT = 409;

    private DeckSaveGuard() {
    }

    /** A short hash of the deck's cards and cards outside the deck, in stored order; "" for no deck. */
    static String revisionOf(SwccgDeck deck) {
        if (deck == null)
            return "";
        String contents = String.join(",", deck.getCards()) + "|" + String.join(",", deck.getCardsOutsideDeck());
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(contents.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++)
                sb.append(String.format("%02x", hash[i]));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Refuses the save with 409 and a message for the player when it would overwrite a deck it must not.
     *
     * @param existing         the deck currently stored under the target name, or null
     * @param createOnly       true for "Save as new deck"
     * @param expectedRevision the revision the editor started from, or null/empty for an unchecked save
     */
    static void check(SwccgDeck existing, boolean createOnly, String expectedRevision) throws HttpProcessingException {
        if (createOnly) {
            if (existing != null)
                throw new HttpProcessingException(CONFLICT,
                        "You already have a deck with that name. Choose a different name for the copy.");
            return;
        }
        if (expectedRevision == null || expectedRevision.isEmpty())
            return;
        if (existing == null)
            throw new HttpProcessingException(CONFLICT,
                    "This deck is no longer saved under that name (deleted or renamed elsewhere). Your edits are kept; save them as a new deck.");
        if (!expectedRevision.equals(revisionOf(existing)))
            throw new HttpProcessingException(CONFLICT,
                    "This deck was changed and saved elsewhere after you opened it. Your edits are kept; save them as a new deck, or reopen the deck to see the newer version.");
    }
}
