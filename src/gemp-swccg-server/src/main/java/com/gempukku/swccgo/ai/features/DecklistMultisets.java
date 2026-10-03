package com.gempukku.swccgo.ai.features;

import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Own-seat decklist helpers. Never accept or emit an opposing list.
 */
public final class DecklistMultisets {

    private DecklistMultisets() {
    }

    /** blueprintId → count from a seated deck's card id list (own side only). */
    public static Map<String, Integer> fromBlueprintIds(List<String> cards) {
        Map<String, Integer> out = new LinkedHashMap<>();
        if (cards == null) {
            return out;
        }
        for (String bp : cards) {
            if (bp == null || bp.isEmpty()) {
                continue;
            }
            out.merge(bp, 1, Integer::sum);
        }
        return out;
    }

    /**
     * Printed destiny hints for blueprints in {@code prior}. Missing blueprints are omitted.
     * Used only to estimate remaining high-destiny density on the owning seat.
     */
    public static Map<String, Float> destinyHints(Map<String, Integer> prior,
                                                  SwccgCardBlueprintLibrary library) {
        Map<String, Float> out = new LinkedHashMap<>();
        if (prior == null || library == null) {
            return out;
        }
        for (String bp : prior.keySet()) {
            if (bp == null || bp.isEmpty()) {
                continue;
            }
            try {
                SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(bp);
                if (blueprint == null) {
                    continue;
                }
                Float destiny = blueprint.getDestiny();
                if (destiny != null) {
                    out.put(bp, destiny);
                }
            } catch (RuntimeException ignored) {
                // omit unknown ids
            }
        }
        return out;
    }
}
