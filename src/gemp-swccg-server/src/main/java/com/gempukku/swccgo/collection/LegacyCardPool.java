package com.gempukku.swccgo.collection;

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
        for (String id : implementedSheetIds(library)) {
            collection.addItem(id, 60);
        }
    }

    public static void appendValidBlueprints(SwccgCardBlueprintLibrary library, SwccgFormat format,
                                             List<SwccgCardBlueprint> blueprints) {
        List<String> ids = new ArrayList<>();
        // The original implementation set is not included in DefaultSwccgFormat's ordinary ranges.
        appendImplementedIds(library, ids, 601, 999);
        ids.addAll(implementedSheetIds(library));
        for (String id : ids) {
            try {
                format.validateCard(id, true);
                blueprints.add(library.getSwccgoCardBlueprint(id));
            } catch (DeckInvalidException excludedFromFormat) {
                // Registration does not change which cards the format permits.
            }
        }
    }

    private static List<String> implementedSheetIds(SwccgCardBlueprintLibrary library) {
        List<String> ids = new ArrayList<>();
        for (int[] sheet : SHEETS) {
            appendImplementedIds(library, ids, sheet[0], sheet[1]);
        }
        return ids;
    }

    private static void appendImplementedIds(SwccgCardBlueprintLibrary library, List<String> ids,
                                              int set, int lastCard) {
        for (int number = 1; number <= lastCard; number++) {
            String id = set + "_" + number;
            if (id.equals(library.getBaseBlueprintId(id)) && library.getSwccgoCardBlueprint(id) != null) {
                ids.add(id);
            }
        }
    }
}
