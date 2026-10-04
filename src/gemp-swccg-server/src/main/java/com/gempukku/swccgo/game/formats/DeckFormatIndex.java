package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.CollectionUtils;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Per-format cache on a deck row: one TEXT blob of triples {@code |code:stamp:P|} or {@code |code:stamp:F|}.
 * Stamp is the format's current rules hash. Missing code is unknown; {@code F} with a matching stamp is a known fail.
 */
public final class DeckFormatIndex {
    public static final String DELIM = "|";
    private static final Pattern TRIPLE = Pattern.compile("^([a-zA-Z0-9_]+):([0-9a-fA-F]{16}):([PF])$");

    public static final class FormatCheck {
        public final String code;
        public final String stamp;
        public final boolean passed;

        public FormatCheck(String code, String stamp, boolean passed) {
            this.code = code;
            this.stamp = stamp;
            this.passed = passed;
        }
    }

    private DeckFormatIndex() {
    }

    public static Map<String, FormatCheck> decode(String encoded) {
        Map<String, FormatCheck> result = new LinkedHashMap<String, FormatCheck>();
        if (encoded == null || encoded.isEmpty()) {
            return result;
        }
        String[] parts = encoded.split("\\|");
        for (String part : parts) {
            if (part == null || part.isEmpty()) {
                continue;
            }
            Matcher matcher = TRIPLE.matcher(part);
            if (!matcher.matches()) {
                continue;
            }
            String code = matcher.group(1);
            result.put(code, new FormatCheck(code, matcher.group(2).toLowerCase(), "P".equals(matcher.group(3))));
        }
        return result;
    }

    public static String encode(Map<String, FormatCheck> checks) {
        if (checks == null || checks.isEmpty()) {
            return DELIM;
        }
        List<String> codes = new ArrayList<String>(checks.keySet());
        Collections.sort(codes);
        StringBuilder sb = new StringBuilder(DELIM);
        for (String code : codes) {
            FormatCheck check = checks.get(code);
            if (check == null || check.code == null || check.stamp == null) {
                continue;
            }
            sb.append(check.code).append(':').append(check.stamp.toLowerCase()).append(':')
                    .append(check.passed ? 'P' : 'F').append(DELIM);
        }
        return sb.toString();
    }

    public static String upsert(String encoded, String formatCode, String stamp, boolean passed) {
        Map<String, FormatCheck> checks = decode(encoded);
        checks.put(formatCode, new FormatCheck(formatCode, stamp, passed));
        return encode(checks);
    }

    public static boolean needsCheck(String encoded, String formatCode, String currentStamp) {
        if (formatCode == null || formatCode.isEmpty() || currentStamp == null || currentStamp.isEmpty()) {
            return false;
        }
        FormatCheck check = decode(encoded).get(formatCode);
        return check == null || !currentStamp.equalsIgnoreCase(check.stamp);
    }

    public static boolean isFreshPass(String encoded, String formatCode, String currentStamp) {
        if (formatCode == null || formatCode.isEmpty() || currentStamp == null) {
            return false;
        }
        FormatCheck check = decode(encoded).get(formatCode);
        return check != null && currentStamp.equalsIgnoreCase(check.stamp) && check.passed;
    }

    public static boolean evaluateAndIndex(SwccgDeck deck, SwccgFormat format, String currentStamp) {
        boolean passed;
        try {
            format.validateDeck(deck);
            passed = true;
        } catch (DeckInvalidException ignored) {
            passed = false;
        } catch (RuntimeException ignored) {
            passed = false;
        }
        deck.setFormatIndex(upsert(deck.getFormatIndex(), format.getCode(), currentStamp, passed));
        return passed;
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
