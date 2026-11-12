package com.gempukku.swccgo.game.setup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A tournament's custom card pool (swccg-guild/work#222), chosen on the Create Tournament form on top of a named
 * format: the sets the event plays (or the format's own sets), plus card-by-card exceptions. An allow admits a card
 * from a set the event leaves out; a ban removes a card from a set it plays. Cards are named by GEMP's blueprint id
 * ({@code 7_150}); a ban or allow covers every printing of that card GEMP maps to it (foils, reprints with the same
 * text). Everything else about the format (deck size, copy limits, side, game options) still applies.
 * <p>
 * Immutable. Carried inside {@link GameSetup}, so it is saved, shown and resolved with the rest of the event's setup and
 * cannot change once the tournament is open (the agreed lock point, Bill 8 Nov 2026). Stored as JSON
 * {@code {"sets":[1,2],"allow":["3_10"],"ban":["1_5"]}}; {@code sets} is left out when the format's sets apply.
 */
public final class CardPool {
    public static final CardPool NONE = new CardPool(null, Collections.emptyList(), Collections.emptyList());

    private final List<Integer> _sets;
    private final List<String> _allowed;
    private final List<String> _banned;

    private CardPool(List<Integer> sets, List<String> allowed, List<String> banned) {
        _sets = sets == null ? null : Collections.unmodifiableList(new ArrayList<>(new TreeSet<>(sets)));
        _allowed = Collections.unmodifiableList(new ArrayList<>(new TreeSet<>(allowed)));
        _banned = Collections.unmodifiableList(new ArrayList<>(new TreeSet<>(banned)));
    }

    /**
     * @param sets    the sets the event plays, or {@code null} for the format's own sets
     * @param allowed blueprint ids admitted from any set
     * @param banned  blueprint ids removed from the pool
     */
    public static CardPool of(List<Integer> sets, List<String> allowed, List<String> banned) {
        List<String> allow = clean(allowed);
        List<String> ban = clean(banned);
        if (sets == null && allow.isEmpty() && ban.isEmpty())
            return NONE;
        return new CardPool(sets == null ? null : new ArrayList<>(new LinkedHashSet<>(sets)), allow, ban);
    }

    private static List<String> clean(List<String> ids) {
        List<String> result = new ArrayList<>();
        if (ids != null)
            for (String id : ids)
                if (id != null && !id.trim().isEmpty())
                    result.add(id.trim());
        return result;
    }

    /** The sets the event plays, sorted, or {@code null} when the format's own sets apply. */
    public List<Integer> getSets() { return _sets; }

    public boolean hasSets() { return _sets != null; }

    /** Blueprint ids admitted from any set, sorted. */
    public List<String> getAllowed() { return _allowed; }

    /** Blueprint ids removed from the pool, sorted. */
    public List<String> getBanned() { return _banned; }

    public boolean isNone() { return _sets == null && _allowed.isEmpty() && _banned.isEmpty(); }

    /** Ids named as both allowed and banned (before printings are considered); the format library checks printings. */
    public Set<String> directConflicts() {
        Set<String> both = new TreeSet<>(_allowed);
        both.retainAll(_banned);
        return both;
    }

    public JsonObject toJsonObject() {
        JsonObject root = new JsonObject();
        if (_sets != null) {
            JsonArray sets = new JsonArray();
            for (Integer set : _sets)
                sets.add(set);
            root.add("sets", sets);
        }
        root.add("allow", strings(_allowed));
        root.add("ban", strings(_banned));
        return root;
    }

    private static JsonArray strings(List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values)
            array.add(value);
        return array;
    }

    public String toJson() {
        return toJsonObject().toString();
    }

    /**
     * Reads a pool from stored or submitted JSON. {@code null}, empty or {@code {}} is {@link #NONE}.
     *
     * @throws IllegalArgumentException with a message for the organizer when the JSON is not a card pool
     */
    public static CardPool fromJson(String json) {
        if (json == null || json.trim().isEmpty())
            return NONE;
        try {
            return fromJsonObject(JsonParser.parseString(json).getAsJsonObject());
        } catch (RuntimeException exp) {
            throw new IllegalArgumentException("The custom card pool could not be read: " + exp.getMessage());
        }
    }

    static CardPool fromJsonObject(JsonObject root) {
        List<Integer> sets = null;
        if (root.has("sets") && root.get("sets").isJsonArray()) {
            sets = new ArrayList<>();
            for (JsonElement set : root.getAsJsonArray("sets"))
                sets.add(set.getAsInt());
        }
        return of(sets, readStrings(root, "allow"), readStrings(root, "ban"));
    }

    private static List<String> readStrings(JsonObject root, String key) {
        List<String> values = new ArrayList<>();
        if (root.has(key) && root.get(key).isJsonArray())
            for (JsonElement value : root.getAsJsonArray(key))
                values.add(value.getAsString());
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CardPool))
            return false;
        CardPool other = (CardPool) o;
        return Objects.equals(_sets, other._sets) && _allowed.equals(other._allowed) && _banned.equals(other._banned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_sets, _allowed, _banned);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
