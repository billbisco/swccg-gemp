package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;

import java.util.ArrayList;
import java.util.List;

/** Enumerates implemented cards from the published Legacy sheets without duplicating PC aliases. */
public final class LegacyCardPool {
    // Last numeric printing position on each published sheet. Includes master-only shield 1200_18.
    private static final int[][] SHEETS = {
            {1200, 18}, {1210, 174}, {1220, 169}, {1230, 118}, {1240, 112}, {1250, 87},
            {1261, 114}, {1262, 26}, {1263, 34}, {1271, 48}, {1272, 28}, {1273, 43},
            {1274, 22}, {1281, 48}, {1282, 28}, {1283, 29}, {1291, 75}, {1292, 11}
    };

    private LegacyCardPool() {
    }

    public static void addSheetCards(SwccgCardBlueprintLibrary library, DefaultCardCollection collection) {
        for (String id : implementedSheetIds(library, true)) {
            collection.addItem(id, 60);
        }
        for (String id : implementedVersionIds(library)) {
            collection.addItem(id, 60);
        }
    }

    /**
     * Pre-errata versions (sets 2200-2292, docs/legacy-versions.md) that have a card class: for each published
     * sheet card, the ids of its older versions k = 0 .. MAX_VERSIONS_PER_CARD - 1.
     */
    public static List<String> implementedVersionIds(SwccgCardBlueprintLibrary library) {
        List<String> ids = new ArrayList<>();
        for (int[] sheet : SHEETS) {
            for (int number = 1; number <= sheet[1]; number++) {
                for (int k = 0; k < LegacyVersion.MAX_VERSIONS_PER_CARD; k++) {
                    String id = LegacyVersion.versionIdOf(sheet[0] + "_" + number, k);
                    // A retired version id aliased to its current card (blueprintMapping.txt) is no version any more.
                    if (library.getBaseBlueprintId(id).equals(id) && library.getSwccgoCardBlueprint(id) != null)
                        ids.add(id);
                }
            }
        }
        return ids;
    }

    /** Numbered printings on the same published sheet keep their own artwork in All Cards decks. */
    public static String defaultDeckPrinting(SwccgCardBlueprintLibrary library, String id) {
        String base = library.getBaseBlueprintId(id);
        if (id.equals(base))
            return id;
        for (int[] sheet : SHEETS) {
            String prefix = sheet[0] + "_";
            if (isNumberedPrinting(id, prefix, sheet[1]) && isNumberedPrinting(base, prefix, sheet[1])
                    && library.getSwccgoCardBlueprint(id) != null)
                return id;
        }
        return base;
    }

    private static boolean isNumberedPrinting(String id, String prefix, int lastCard) {
        if (!id.startsWith(prefix))
            return false;
        String number = id.substring(prefix.length());
        if (number.isEmpty() || number.length() > 3 || number.charAt(0) == '0'
                || !number.chars().allMatch(c -> c >= '0' && c <= '9'))
            return false;
        return Integer.parseInt(number) <= lastCard;
    }

    public static void appendValidBlueprints(SwccgCardBlueprintLibrary library, SwccgFormat format,
                                             List<SwccgCardBlueprint> blueprints) {
        List<String> ids = new ArrayList<>();
        // The original implementation set is not included in DefaultSwccgFormat's ordinary ranges.
        appendImplementedIds(library, ids, 601, 999, false);
        ids.addAll(implementedSheetIds(library, false));
        ids.addAll(implementedVersionIds(library));
        for (String id : ids) {
            try {
                format.validateCard(id, true);
                blueprints.add(library.getSwccgoCardBlueprint(id));
            } catch (DeckInvalidException excludedFromFormat) {
                // Registration does not change which cards the format permits.
            }
        }
    }

    private static List<String> implementedSheetIds(SwccgCardBlueprintLibrary library, boolean includePrintings) {
        List<String> ids = new ArrayList<>();
        for (int[] sheet : SHEETS) {
            appendImplementedIds(library, ids, sheet[0], sheet[1], includePrintings);
        }
        return ids;
    }

    private static void appendImplementedIds(SwccgCardBlueprintLibrary library, List<String> ids,
                                              int set, int lastCard, boolean includePrintings) {
        for (int number = 1; number <= lastCard; number++) {
            String id = set + "_" + number;
            String printing = includePrintings ? defaultDeckPrinting(library, id) : library.getBaseBlueprintId(id);
            if (id.equals(printing) && library.getSwccgoCardBlueprint(id) != null) {
                ids.add(id);
            }
        }
    }
}
