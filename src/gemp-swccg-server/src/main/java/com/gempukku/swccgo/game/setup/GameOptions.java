package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.game.DeckSize;
import com.gempukku.swccgo.game.StartingCredits;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.GameOption.Kind;
import com.gempukku.swccgo.game.setup.GameOption.Scope;
import com.gempukku.swccgo.game.setup.GameOption.Type;
import com.gempukku.swccgo.hall.HallGameTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The registry of game options (swccg-guild/work#114/#116). Adding an option here is all the setup form, validation,
 * chips and the stored setup need; its effect on the game is wired where the rule lives.
 */
public final class GameOptions {
    public static final String DECK_SIZE = "deckSize";
    public static final String STARTING_CREDITS = "startingCredits";
    public static final String TIMER = "timer";
    public static final String OBJECTIVE_OPTIONAL = "objectiveOptional";
    public static final String NO_SHIELDS = "noShields";

    private static final Map<String, GameOption> OPTIONS = new LinkedHashMap<>();

    static {
        register(GameOption.builder(DECK_SIZE, Kind.DECK_RULES, Type.INTEGER, "Deck size")
                .help("Every deck in this game must have exactly this many cards. Empty uses the format's own size.")
                .defaultValue(f -> f.getMinimumDeckSize() == f.getMaximumDeckSize() ? String.valueOf(f.getRequiredDeckSize()) : null)
                .defaultLabel(f -> f.getMinimumDeckSize() == f.getMaximumDeckSize()
                        ? String.valueOf(f.getRequiredDeckSize())
                        : f.getMinimumDeckSize() + "–" + f.getMaximumDeckSize())
                .range(f -> DeckSize.MIN_SUPPORTED, DeckSize::maxFor)
                .shortLabel(v -> v + " cards")
                .build());
        register(GameOption.builder(STARTING_CREDITS, Kind.PLAY_RULES, Type.INTEGER, "Starting credits")
                .help("Per player; 0 uses random opening hands.")
                .availableWhen(SwccgFormat::supportsStartingCredits)
                .defaultValue(f -> String.valueOf(f.getDefaultStartingCredits()))
                .range(f -> 0, f -> StartingCredits.MAX_BUDGET)
                .shortLabel(v -> v + " starting credits")
                .build());
        // #142: on by default where a deck may hold several Objectives (Anything Goes), off elsewhere
        register(GameOption.builder(OBJECTIVE_OPTIONAL, Kind.PLAY_RULES, Type.BOOLEAN, "Starting: Objective optional")
                .help("At game start, choose your Objective or one of your starting locations; Objectives not played stay in the deck (destiny 0).")
                .defaultValue(f -> String.valueOf(f.allowsMultipleObjectives()))
                .shortLabel(v -> "true".equals(v) ? "Objective optional" : "Objective required")
                .build());
        // #144: a deck rule, as Open (no shields): the table refuses decks holding Defensive Shields
        register(GameOption.builder(NO_SHIELDS, Kind.DECK_RULES, Type.BOOLEAN, "Shields: none")
                .help("Decks may not hold Defensive Shields, as in Open (no shields). Starting Effects are still allowed.")
                .availableWhen(f -> !f.getBannedIcons().contains("DEFENSIVE_SHIELD"))
                .defaultValue(f -> "false")
                .shortLabel(v -> "true".equals(v) ? "No shields" : "Shields allowed")
                .build());
        GameOption.Builder timer = GameOption.builder(TIMER, Kind.TABLE, Type.CHOICE, "Game timer")
                .help("Each player's time bank and the time per decision.")
                .scopes(Scope.CASUAL)
                .defaultValue(f -> HallGameTimer.DEFAULT.getCode());
        for (HallGameTimer t : HallGameTimer.values())
            timer.choice(t.getCode(), t.getLabel(), t.getShortLabel());
        register(timer.build());
    }

    private GameOptions() {
    }

    /** Adds an option to the registry. Package and tests only; production options are registered above. */
    public static synchronized void register(GameOption option) {
        OPTIONS.put(option.getId(), option);
    }

    /** Removes an option (tests only). */
    public static synchronized void unregister(String id) {
        OPTIONS.remove(id);
    }

    public static synchronized GameOption get(String id) {
        return OPTIONS.get(id);
    }

    /** Every option, in display order: by kind, then in the order registered. */
    public static synchronized List<GameOption> all() {
        List<GameOption> list = new ArrayList<>(OPTIONS.values());
        list.sort(Comparator.comparing(GameOption::getKind));
        return Collections.unmodifiableList(list);
    }
}
