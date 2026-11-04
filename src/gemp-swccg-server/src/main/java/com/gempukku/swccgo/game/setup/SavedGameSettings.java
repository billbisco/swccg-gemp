package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

/**
 * Checks game settings a player saves for a setup form and format before they are stored (swccg-guild/work#135).
 * Only real options, valid for the format, are kept; values equal to the format's defaults are dropped.
 */
public final class SavedGameSettings {
    /** The setup forms that save settings. */
    public static final Set<String> FORMS = Set.of("play", "tournament");
    private static final int MAX_OPTIONS = 32;

    private SavedGameSettings() {
    }

    /**
     * The JSON to store (an object of option id to value), or {@code "{}"} when nothing differs from the defaults.
     *
     * @throws IllegalArgumentException with a message for the player when the form, format or a value is not valid
     */
    public static String normalize(SwccgoFormatLibrary formats, String form, String formatCode, Map<String, String> raw) {
        if (form == null || !FORMS.contains(form))
            throw new IllegalArgumentException("Unknown settings form: " + form);
        SwccgFormat format = formatCode == null || formatCode.length() > 100 ? null : formats.getFormat(formatCode);
        if (format == null || format.isDeckSizeChosen())
            throw new IllegalArgumentException("Unknown format: " + formatCode);
        if (raw != null && raw.size() > MAX_OPTIONS)
            throw new IllegalArgumentException("Too many settings");
        GameSetup setup = GameSetup.of(format, raw, null);
        JsonObject json = new JsonObject();
        for (GameOption option : GameOptions.all()) {   // registry order, so the stored text is stable
            String value = setup.getChosen(option.getId());
            if (value != null)
                json.addProperty(option.getId(), value);
        }
        return json.toString();
    }
}
