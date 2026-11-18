package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.game.SwccgFormat;

import java.util.List;
import java.util.Map;

/** Approved, partial setup shortcuts. The ordinary setup remains the only rules authority. */
public final class GameRulePresets {
    public static final String LIMITATION = "Only operative restrictions and Cloud City Projective Telepathy text "
            + "are reconstructed. Other rules and card errata use the current engine; this is not a complete historical ruleset.";

    public record Preset(String id, String label, String formatCode, Map<String, String> values, String sourceNote) {
        public Preset {
            values = Map.copyOf(values);
        }

        public GameSetup setup(SwccgFormat format, GameOption.Scope scope) {
            if (!formatCode.equals(format.getBaseCode()))
                throw new IllegalArgumentException("This partial preset requires its approved base format.");
            return GameSetup.of(format, values, scope);
        }
    }

    private static final List<Preset> PRESETS = List.of(
            new Preset("early-operative", "Premiere–Special Edition: early operative style (partial rules)",
                    "premiere_se", Map.of(GameOptions.OPERATIVE_RULE, "false", GameOptions.PROJECTIVE_TELEPATHY, "printed"),
                    "November 1998 glossary and original Cloud City card. This does not reconstruct a complete event ruleset."),
            new Preset("may-2001", "Premiere–Death Star II: May 2001 checkpoint (partial rules)",
                    "premiere_ds2", Map.of(GameOptions.OPERATIVE_RULE, "true", GameOptions.PROJECTIVE_TELEPATHY, "current"),
                    "Decipher supplement dated 30 May 2001, pages 9 and 26. This is a documented checkpoint, not the first erratum date."));

    private GameRulePresets() { }

    public static List<Preset> all() {
        return PRESETS;
    }

    /** Derive the label from actual choices, never from an untrusted or stale preset name. */
    public static Preset matching(GameSetup setup, SwccgFormat format) {
        if (!setup.getPool().isNone())
            return null;
        for (Preset preset : PRESETS) {
            if (preset.formatCode().equals(setup.getFormatCode()) && preset.formatCode().equals(format.getBaseCode())
                    && preset.values().entrySet().stream().allMatch(e -> e.getValue().equals(setup.get(e.getKey(), format))))
                return preset;
        }
        return null;
    }
}
