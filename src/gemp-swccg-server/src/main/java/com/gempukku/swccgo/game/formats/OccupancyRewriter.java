package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Format-time rewrite of former PC ids using the Virtual Modern occupancy table.
 * A saved deck keeps the old id; a format with {@code occupancyDate} decides which
 * 701/702 printing plays. Current Open / Classic / Jawa / Legacy have no date.
 * Anything Goes must not rewrite (both ids are legal).
 */
public class OccupancyRewriter {
    static final class Row {
        final String formerId;
        final LocalDate until;
        final String newId;
        final boolean blanked;

        Row(String formerId, LocalDate until, String newId, boolean blanked) {
            this.formerId = formerId;
            this.until = until;
            this.newId = newId;
            this.blanked = blanked;
        }
    }

    private static final Map<String, Row> VIRTUAL_MODERN = load("/occupancy-virtual-modern.json");

    static Map<String, Row> virtualModern() {
        return VIRTUAL_MODERN;
    }

    static int replacedCount() {
        int n = 0;
        for (Row row : VIRTUAL_MODERN.values()) {
            if (!row.blanked)
                n++;
        }
        return n;
    }

    static int blankedCount() {
        int n = 0;
        for (Row row : VIRTUAL_MODERN.values()) {
            if (row.blanked)
                n++;
        }
        return n;
    }

    public static boolean isFormerId(String id) {
        return id != null && VIRTUAL_MODERN.containsKey(baseId(id));
    }

    /**
     * If {@code date} is strictly before the row's until date, replace the former PC id
     * with the 701/702 id. On or after until, leave the id (replaced: current occupant;
     * blanked: no class).
     */
    public static String apply(String id, LocalDate date) {
        return apply(id, date, VIRTUAL_MODERN);
    }

    static String apply(String id, LocalDate date, Map<String, Row> table) {
        if (id == null || date == null || table == null || table.isEmpty())
            return id;
        String base = baseId(id);
        Row row = table.get(base);
        if (row == null)
            return id;
        if (date.isBefore(row.until))
            return row.newId + id.substring(base.length());
        return id;
    }

    static List<String> apply(List<String> ids, LocalDate date, Map<String, Row> table) {
        if (ids == null)
            return null;
        List<String> rewritten = new ArrayList<String>(ids.size());
        for (String id : ids)
            rewritten.add(apply(id, date, table));
        return rewritten;
    }

    public static SwccgDeck applyDeck(SwccgDeck deck, LocalDate date) {
        return applyDeck(deck, date, VIRTUAL_MODERN);
    }

    static SwccgDeck applyDeck(SwccgDeck deck, LocalDate date, Map<String, Row> table) {
        if (deck == null || date == null)
            return deck;
        return DeckCardResolver.apply(deck, id -> apply(id, date, table));
    }

    static String baseId(String id) {
        if (id == null)
            return null;
        int slash = id.indexOf('/');
        if (slash >= 0)
            id = id.substring(0, slash);
        return id.replace("*", "").replace("^", "");
    }

    private static Map<String, Row> load(String resource) {
        try (InputStreamReader reader = new InputStreamReader(
                OccupancyRewriter.class.getResourceAsStream(resource), StandardCharsets.UTF_8)) {
            JSONObject root = (JSONObject) new JSONParser().parse(reader);
            Map<String, Row> table = new LinkedHashMap<String, Row>();
            addRows(table, (JSONArray) root.get("replaced"), false);
            addRows(table, (JSONArray) root.get("blanked"), true);
            return Collections.unmodifiableMap(table);
        } catch (Exception exp) {
            throw new RuntimeException("Problem loading occupancy table " + resource, exp);
        }
    }

    private static void addRows(Map<String, Row> table, JSONArray rows, boolean blanked) {
        if (rows == null)
            return;
        for (Object o : rows) {
            JSONObject row = (JSONObject) o;
            String formerId = (String) row.get("formerId");
            String newId = (String) row.get("newId");
            LocalDate until = LocalDate.parse((String) row.get("until"));
            table.put(formerId, new Row(formerId, until, newId, blanked));
        }
    }
}
