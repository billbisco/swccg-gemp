package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.CollectionUtils;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Save-time index of which format codes a deck currently passes.
 * Encoded as |code|code| so substring matches cannot confuse premiere with premiere_anh.
 */
public final class DeckFormatIndex {
    public static final String DELIM = "|";

    private DeckFormatIndex() {
    }

    public static String encode(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return DELIM;
        }
        StringBuilder sb = new StringBuilder(DELIM);
        for (String code : codes) {
            if (code != null && !code.isEmpty()) {
                sb.append(code).append(DELIM);
            }
        }
        return sb.toString();
    }

    public static boolean containsFormat(String encoded, String formatCode) {
        if (encoded == null || formatCode == null || formatCode.isEmpty()) {
            return false;
        }
        return encoded.contains(DELIM + formatCode + DELIM);
    }

    public static String computeValidFormats(SwccgDeck deck, SwccgoFormatLibrary formats) {
        List<String> codes = new ArrayList<String>();
        for (Map.Entry<String, SwccgFormat> entry : formats.getAllFormats().entrySet()) {
            try {
                entry.getValue().validateDeck(deck);
                codes.add(entry.getKey());
            } catch (DeckInvalidException ignored) {
            } catch (RuntimeException ignored) {
            }
        }
        Collections.sort(codes);
        return encode(codes);
    }

    public static boolean isConstructedCollection(String collectionCode) {
        return collectionCode == null || collectionCode.isEmpty()
                || "default".equals(collectionCode) || "permanent".equals(collectionCode);
    }

    public static boolean deckOwnedInCollection(SwccgDeck deck, CardCollection collection) {
        if (deck == null || collection == null) {
            return false;
        }
        Map<String, Integer> counts = CollectionUtils.getTotalCardCountForDeck(deck);
        for (Map.Entry<String, Integer> cardCount : counts.entrySet()) {
            if (collection.getItemCount(cardCount.getKey()) < cardCount.getValue()) {
                return false;
            }
        }
        return true;
    }
}
