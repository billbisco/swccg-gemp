package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.GameOption.Scope;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A game's setup (swccg-guild/work#114/#116): the format, the mode, and the options chosen at setup. Only values that
 * differ from the format's defaults are kept, so an empty setup is "the format as it is". Immutable.
 * <p>
 * Stored as versioned JSON: {@code {"v":1,"format":"open","mode":"1v1","options":{"deckSize":"100"}}}.
 */
public final class GameSetup {
    public static final int VERSION = 1;
    public static final String MODE_1V1 = "1v1";

    private final String _formatCode;
    private final String _mode;
    private final Map<String, String> _values;

    private GameSetup(String formatCode, String mode, Map<String, String> values) {
        _formatCode = formatCode;
        _mode = mode == null || mode.isEmpty() ? MODE_1V1 : mode;
        _values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    /** The format with every option at its default. */
    public static GameSetup defaults(String formatCode) {
        return new GameSetup(formatCode, MODE_1V1, Collections.emptyMap());
    }

    /**
     * Builds a setup from values typed or picked at setup. Empty values mean "the default"; values equal to the format's
     * default are dropped.
     *
     * @throws IllegalArgumentException with a message for the player when an option is unknown, not available for this
     *                                  format, not allowed in this kind of game, or out of range
     */
    public static GameSetup of(SwccgFormat format, Map<String, String> raw, Scope scope) {
        Map<String, String> values = new LinkedHashMap<>();
        if (raw != null) {
            for (Map.Entry<String, String> entry : raw.entrySet()) {
                String value = entry.getValue();
                if (value == null || value.trim().isEmpty())
                    continue;
                GameOption option = GameOptions.get(entry.getKey());
                if (option == null)
                    throw new IllegalArgumentException("Unknown game option: " + entry.getKey());
                if (!option.isAvailable(format))
                    throw new IllegalArgumentException(option.getLabel() + " is not used in " + format.getName());
                String normalized = option.normalize(value, format);
                if (normalized.equals(option.getDefault(format)))
                    continue;
                if (scope != null && !option.isAllowedIn(scope))
                    throw new IllegalArgumentException(option.getLabel() + " can't be changed in this kind of game");
                values.put(option.getId(), normalized);
            }
        }
        return new GameSetup(format.getBaseCode(), MODE_1V1, values);
    }

    public String getFormatCode() { return _formatCode; }
    public String getMode() { return _mode; }

    /** The values that differ from the format's defaults, by option id. */
    public Map<String, String> getValues() { return _values; }

    public boolean isDefault() {
        return _values.isEmpty() && MODE_1V1.equals(_mode);
    }

    /** The chosen value, or {@code null} when the option is at the format's default. */
    public String getChosen(String optionId) {
        return _values.get(optionId);
    }

    /** The value in effect: the chosen one, or the format's default (which may itself be {@code null}). */
    public String get(String optionId, SwccgFormat format) {
        String chosen = _values.get(optionId);
        if (chosen != null)
            return chosen;
        GameOption option = GameOptions.get(optionId);
        return option == null ? null : option.getDefault(format);
    }

    /** A copy with one more value (or with it removed when {@code value} is null). Not re-validated. */
    public GameSetup with(String optionId, String value) {
        Map<String, String> values = new LinkedHashMap<>(_values);
        if (value == null)
            values.remove(optionId);
        else
            values.put(optionId, value);
        return new GameSetup(_formatCode, _mode, values);
    }

    /** A copy for another format code (for example a tournament product's format). */
    public GameSetup forFormat(String formatCode) {
        return new GameSetup(formatCode, _mode, _values);
    }

    /** Short labels of the non-default options, in display order ("100 cards", "Blitz"). */
    public List<String> chips() {
        List<String> chips = new ArrayList<>();
        for (GameOption option : GameOptions.all()) {
            String value = _values.get(option.getId());
            if (value != null)
                chips.add(option.getShortLabel(value));
        }
        return chips;
    }

    /** The chips joined for a list cell, or "" when every option is at its default. */
    public String chipsText() {
        return String.join(" · ", chips());
    }

    /** Every option that applies to the format, with its value in effect, for the full setup view. */
    public Map<String, String> describe(SwccgFormat format) {
        Map<String, String> result = new LinkedHashMap<>();
        for (GameOption option : GameOptions.all()) {
            if (!option.isAvailable(format))
                continue;
            String chosen = _values.get(option.getId());
            result.put(option.getLabel(), chosen != null ? option.getValueLabel(chosen) : option.getDefaultLabel(format) + " (default)");
        }
        return result;
    }

    private static final Gson GSON = new Gson();

    public String toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("v", VERSION);
        root.addProperty("format", _formatCode);
        root.addProperty("mode", _mode);
        JsonObject options = new JsonObject();
        for (Map.Entry<String, String> entry : _values.entrySet())
            options.addProperty(entry.getKey(), entry.getValue());
        root.add("options", options);
        return GSON.toJson(root);
    }

    /**
     * Reads a stored setup. {@code null} or empty JSON means the format's defaults (rows from before #116). Unknown
     * options are kept, so a newer build's setup survives a round trip through an older one.
     */
    public static GameSetup fromJson(String json, String formatCode) {
        if (json == null || json.trim().isEmpty())
            return defaults(formatCode);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        String format = root.has("format") && !root.get("format").isJsonNull() ? root.get("format").getAsString() : formatCode;
        String mode = root.has("mode") && !root.get("mode").isJsonNull() ? root.get("mode").getAsString() : MODE_1V1;
        Map<String, String> values = new LinkedHashMap<>();
        if (root.has("options") && root.get("options").isJsonObject()) {
            for (Map.Entry<String, com.google.gson.JsonElement> entry : root.getAsJsonObject("options").entrySet())
                values.put(entry.getKey(), entry.getValue().getAsString());
        }
        return new GameSetup(format, mode, values);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameSetup))
            return false;
        GameSetup other = (GameSetup) o;
        return Objects.equals(_formatCode, other._formatCode) && _mode.equals(other._mode) && _values.equals(other._values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_formatCode, _mode, _values);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
