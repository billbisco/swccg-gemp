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
    public static final String SHIELDS_NO_PLAY_LIMIT = "shieldsNoPlayLimit";
    public static final String STARTING_CARDS = "startingCards";
    public static final String STARTING_INTERRUPTS = "startingInterrupts";
    public static final String STARTING_EFFECTS = "startingEffects";
    public static final String DECIPHER_LOCATION_RULES = "decipherLocationRules";
    public static final String OBJECTIVES_FROM_HAND = "objectivesFromHand";
    /** Most starting locations and Objectives a player may choose (#142 R2a). */
    public static final int STARTING_CARDS_MAX = 10;

    private static final Map<String, GameOption> OPTIONS = new LinkedHashMap<>();

    static {
        // #175: labels and chips read "Area: setting" (Bill 2026-11-04), and the form lists options by area in the
        // order registered here: Deck, Shields, Starting, Objectives, Timer.
        register(GameOption.builder(DECK_SIZE, Kind.DECK_RULES, Type.INTEGER, "Deck: size")
                .help("Every deck in this game must have exactly this many cards. Empty uses the format's own size.")
                .defaultValue(f -> f.getMinimumDeckSize() == f.getMaximumDeckSize() ? String.valueOf(f.getRequiredDeckSize()) : null)
                .defaultLabel(f -> f.getMinimumDeckSize() == f.getMaximumDeckSize()
                        ? String.valueOf(f.getRequiredDeckSize())
                        : f.getMinimumDeckSize() + "–" + f.getMaximumDeckSize())
                .range(f -> DeckSize.MIN_SUPPORTED, DeckSize::maxFor)
                .shortLabel(v -> "Deck: " + v + " cards")
                .build());
        // #144: a deck rule, as Open (no shields): the table refuses decks holding Defensive Shields
        register(GameOption.builder(NO_SHIELDS, Kind.DECK_RULES, Type.BOOLEAN, "Shields: none")
                .help("Decks may not hold Defensive Shields, as in Open (no shields). Starting Effects are still allowed.")
                .availableWhen(f -> !f.getBannedIcons().contains("DEFENSIVE_SHIELD"))
                .defaultValue(f -> "false")
                .shortLabel(v -> "true".equals(v) ? "Shields: none" : "Shields: allowed")
                .build());
        // #145: lifts the Starting Effects' times-per-game shield plays and their 10 / 15 caps (deck and stacking)
        register(GameOption.builder(SHIELDS_NO_PLAY_LIMIT, Kind.PLAY_RULES, Type.BOOLEAN, "Shields: no play limit")
                .help("Starting Effects may play any number of Defensive Shields, and hold any number. Their timing rules still apply.")
                .availableWhen(f -> !f.getBannedIcons().contains("DEFENSIVE_SHIELD"))
                .defaultValue(f -> "false")
                .shortLabel(v -> "true".equals(v) ? "Shields: no play limit" : "Shields: play limit")
                .build());
        register(GameOption.builder(STARTING_CREDITS, Kind.PLAY_RULES, Type.INTEGER, "Starting: credits")
                .help("Per player; 0 uses random opening hands.")
                .availableWhen(SwccgFormat::supportsStartingCredits)
                .defaultValue(f -> String.valueOf(f.getDefaultStartingCredits()))
                .range(f -> 0, f -> StartingCredits.MAX_BUDGET)
                .shortLabel(v -> "Starting: " + v + " credits")
                .build());
        // #142: on by default where a deck may hold several Objectives (Anything Goes), off elsewhere
        register(GameOption.builder(OBJECTIVE_OPTIONAL, Kind.PLAY_RULES, Type.BOOLEAN, "Starting: Objective optional")
                .help("At game start, choose your Objective or one of your starting locations; Objectives not played stay in the deck (destiny 0).")
                .defaultValue(f -> String.valueOf(f.allowsMultipleObjectives()))
                .shortLabel(v -> "true".equals(v) ? "Starting: Objective optional" : "Starting: Objective required")
                .build());
        // #142 R2a: needs "Starting: Objective optional" on (Bill 2026-11-04)
        register(GameOption.builder(STARTING_CARDS, Kind.PLAY_RULES, Type.INTEGER, "Starting: locations/Objectives")
                .help("How many starting locations and Objectives each player may choose at game start (with Starting: Objective optional on).")
                .defaultValue(f -> "1")
                .range(f -> 1, f -> STARTING_CARDS_MAX)
                .shortLabel(v -> "Starting: " + v + " locations/Objectives")
                .build());
        // #142 R2b/R2c
        register(GameOption.builder(STARTING_INTERRUPTS, Kind.PLAY_RULES, Type.INTEGER, "Starting: Interrupts")
                .help("How many Starting Interrupts each player may play at game start. A unique one is played once.")
                .defaultValue(f -> "1")
                .range(f -> 1, f -> 5)
                .shortLabel(v -> "Starting: " + v + " Interrupts")
                .build());
        register(GameOption.builder(STARTING_EFFECTS, Kind.DECK_RULES, Type.INTEGER, "Starting: Effects")
                .help("How many Starting Effects a deck may hold; each is deployed at game start.")
                .defaultValue(f -> "1")
                .range(f -> 1, f -> 5)
                .shortLabel(v -> "Starting: " + v + " Effects")
                .build());
        // #143
        register(GameOption.builder(DECIPHER_LOCATION_RULES, Kind.PLAY_RULES, Type.BOOLEAN, "Starting: Decipher location rules")
                .help("Both players choosing the same unique starting location set it aside and choose again, with no conversion offered, as in Decipher's rules.")
                .defaultValue(f -> "false")
                .shortLabel(v -> "true".equals(v) ? "Starting: Decipher location rules" : "Starting: PC location rules")
                .build());
        // #142 R3 (Bill 2026-11-05, design A1): on by default where a deck may hold several Objectives (Anything Goes)
        register(GameOption.builder(OBJECTIVES_FROM_HAND, Kind.PLAY_RULES, Type.BOOLEAN, "Objectives: deploy from hand")
                .help("During your deploy phase, deploy an Objective from hand for free when the cards its setup deploys are in your hand or already on the table; the setup takes them from hand.")
                .defaultValue(f -> String.valueOf(f.allowsMultipleObjectives()))
                .shortLabel(v -> "true".equals(v) ? "Objectives: from hand" : "Objectives: at start only")
                .build());
        GameOption.Builder timer = GameOption.builder(TIMER, Kind.TABLE, Type.CHOICE, "Timer: game")
                .help("Each player's time bank and the time per decision.")
                .scopes(Scope.CASUAL)
                .defaultValue(f -> HallGameTimer.DEFAULT.getCode());
        for (HallGameTimer t : HallGameTimer.values())
            timer.choice(t.getCode(), t.getLabel(), "Timer: " + t.getShortLabel());
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

    /** Every option, in display order: the order registered, grouped by area (#175). */
    public static synchronized List<GameOption> all() {
        return Collections.unmodifiableList(new ArrayList<>(OPTIONS.values()));
    }
}
