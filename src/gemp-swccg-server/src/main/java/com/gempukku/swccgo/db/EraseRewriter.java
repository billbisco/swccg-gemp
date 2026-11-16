package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.save.DecisionLog;
import com.gempukku.swccgo.game.save.GameSaveHeader;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.Map;

/**
 * Close account (#524 follow-up): rewrites the closed member's old name to the placeholder inside stored game
 * setups and other members' saved games. Only exact matches of the whole name change.
 */
final class EraseRewriter {
    private static final Gson GSON = new Gson();

    private EraseRewriter() {
    }

    /** The setup JSON with every string value equal to {@code oldName} replaced, or null when nothing changed. */
    static String setup(String json, String oldName, String placeholder) {
        if (json == null || json.isEmpty() || !json.contains(oldName))
            return null;
        JsonElement root;
        try {
            root = JsonParser.parseString(json);
        } catch (RuntimeException e) {
            return null;
        }
        return replace(root, oldName, placeholder) ? GSON.toJson(root) : null;
    }

    private static boolean replace(JsonElement e, String oldName, String placeholder) {
        boolean changed = false;
        if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
                JsonElement v = entry.getValue();
                if (isName(v, oldName)) {
                    entry.setValue(new JsonPrimitive(placeholder));
                    changed = true;
                } else {
                    changed |= replace(v, oldName, placeholder);
                }
            }
        } else if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            for (int i = 0; i < a.size(); i++) {
                if (isName(a.get(i), oldName)) {
                    a.set(i, new JsonPrimitive(placeholder));
                    changed = true;
                } else {
                    changed |= replace(a.get(i), oldName, placeholder);
                }
            }
        }
        return changed;
    }

    private static boolean isName(JsonElement v, String oldName) {
        return v.isJsonPrimitive() && v.getAsJsonPrimitive().isString() && v.getAsString().equals(oldName);
    }

    /** A rewritten saved game: header JSON and move log. */
    record Save(String header, byte[] log) {
    }

    /**
     * Another member's saved game with the closed member in a seat: the seat and the move log show the placeholder.
     * Position hashes include player names, so the rewritten log drops them (the replay is not checked) and the
     * caller drops the snapshot. Returns null when the closed member isn't in the game.
     */
    static Save save(String headerJson, byte[] log, String oldName, String placeholder) {
        GameSaveHeader h = GameSaveHeader.fromJson(headerJson);
        boolean seated = false;
        for (GameSaveHeader.Seat s : h.seats) {
            if (oldName.equals(s.playerId)) {
                s.playerId = placeholder;
                seated = true;
            }
            if (oldName.equals(s.displayName)) {
                s.displayName = placeholder;
                seated = true;
            }
        }
        if (!seated)
            return null;
        DecisionLog in = DecisionLog.fromBytes(log);
        DecisionLog out = new DecisionLog();
        for (DecisionLog.Entry e : in.getEntries()) {
            out.add(oldName.equals(e.player()) ? placeholder : e.player(),
                    oldName.equals(e.answer()) ? placeholder : e.answer(), null);
        }
        return new Save(h.toJson(), out.toBytes());
    }
}
