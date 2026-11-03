package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.game.SwccgFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * One game option (swccg-guild/work#114/#116): a setting chosen at setup next to the format, such as deck size,
 * starting credits or the timer. Options are defined once, in {@link GameOptions}; the setup form, validation, the
 * short labels shown in lists and the stored setup all come from this definition.
 */
public final class GameOption {
    /** The four kinds of option (#114). The order is the order chips are shown in. */
    public enum Kind {
        /** How many sides and seats (1v1, 2v2, free-for-all, Sabacc). Reserved; only 1v1 today. */
        MODE,
        /** What a deck must be (deck size, same side, errata set). */
        DECK_RULES,
        /** What the engine does (Operative Rule, starting credits, ...). */
        PLAY_RULES,
        /** Not rules: timer, reverts, who can join or watch. */
        TABLE
    }

    public enum Type { BOOLEAN, INTEGER, CHOICE }

    /** Where an option may be set. */
    public enum Scope { CASUAL, BOT, LEAGUE, TOURNAMENT }

    /** One value of a {@link Type#CHOICE} option. */
    public static final class Choice {
        private final String _value;
        private final String _label;
        private final String _shortLabel;

        public Choice(String value, String label, String shortLabel) {
            _value = value;
            _label = label;
            _shortLabel = shortLabel;
        }

        public String getValue() { return _value; }
        public String getLabel() { return _label; }
        public String getShortLabel() { return _shortLabel; }
    }

    private final String _id;
    private final Kind _kind;
    private final Type _type;
    private final String _label;
    private final String _help;
    private final Set<Scope> _scopes;
    private final Predicate<SwccgFormat> _available;
    private final Function<SwccgFormat, String> _default;
    private final Function<SwccgFormat, String> _defaultLabel;
    private final ToIntFunction<SwccgFormat> _min;
    private final ToIntFunction<SwccgFormat> _max;
    private final List<Choice> _choices;
    private final Function<String, String> _shortLabel;

    private GameOption(Builder b) {
        _id = b._id;
        _kind = b._kind;
        _type = b._type;
        _label = b._label;
        _help = b._help;
        _scopes = Collections.unmodifiableSet(EnumSet.copyOf(b._scopes));
        _available = b._available;
        _default = b._default;
        _defaultLabel = b._defaultLabel;
        _min = b._min;
        _max = b._max;
        _choices = Collections.unmodifiableList(new ArrayList<>(b._choices));
        _shortLabel = b._shortLabel;
    }

    public static Builder builder(String id, Kind kind, Type type, String label) {
        return new Builder(id, kind, type, label);
    }

    public String getId() { return _id; }
    public Kind getKind() { return _kind; }
    public Type getType() { return _type; }
    public String getLabel() { return _label; }
    public String getHelp() { return _help; }
    public Set<Scope> getScopes() { return _scopes; }
    public List<Choice> getChoices() { return _choices; }

    public boolean isAllowedIn(Scope scope) {
        return _scopes.contains(scope);
    }

    /** Whether the option applies to a format at all (for example starting credits only in Reflections Gold formats). */
    public boolean isAvailable(SwccgFormat format) {
        return _available.test(format);
    }

    /** The format's default value, or {@code null} when the format has no single default (a 40–60 deck size range). */
    public String getDefault(SwccgFormat format) {
        return _default.apply(format);
    }

    /** How the default reads in the form, for example "60" or "40–60". */
    public String getDefaultLabel(SwccgFormat format) {
        String label = _defaultLabel != null ? _defaultLabel.apply(format) : null;
        if (label != null)
            return label;
        String value = getDefault(format);
        Choice choice = choice(value);
        return choice != null ? choice.getLabel() : value;
    }

    public int getMin(SwccgFormat format) { return _min.applyAsInt(format); }
    public int getMax(SwccgFormat format) { return _max.applyAsInt(format); }

    /** The short label shown in lists ("100 cards", "Blitz"). */
    public String getShortLabel(String value) {
        if (_shortLabel != null)
            return _shortLabel.apply(value);
        Choice choice = choice(value);
        if (choice != null)
            return choice.getShortLabel();
        return _type == Type.BOOLEAN ? ("true".equals(value) ? _label : "No " + _label) : (_label + " " + value);
    }

    /** The long label for a value, for the full setup view. */
    public String getValueLabel(String value) {
        Choice choice = choice(value);
        if (choice != null)
            return choice.getLabel();
        if (_type == Type.BOOLEAN)
            return "true".equals(value) ? "On" : "Off";
        return value;
    }

    Choice choice(String value) {
        if (value == null)
            return null;
        for (Choice choice : _choices)
            if (choice.getValue().equalsIgnoreCase(value))
                return choice;
        return null;
    }

    /**
     * The canonical form of a value typed or picked at setup, checked against this format.
     *
     * @throws IllegalArgumentException with a message for the player when the value is not allowed
     */
    public String normalize(String raw, SwccgFormat format) {
        String value = raw == null ? "" : raw.trim();
        switch (_type) {
            case BOOLEAN:
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value.equals("1"))
                    return "true";
                if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off") || value.equals("0"))
                    return "false";
                throw new IllegalArgumentException(_label + " must be on or off");
            case INTEGER: {
                int min = getMin(format);
                int max = getMax(format);
                if (!value.matches("[0-9]{1,6}") || Integer.parseInt(value) < min || Integer.parseInt(value) > max)
                    throw new IllegalArgumentException(_label + " must be a whole number from " + min + " to " + max);
                return String.valueOf(Integer.parseInt(value));
            }
            case CHOICE:
            default: {
                Choice choice = choice(value);
                if (choice == null)
                    throw new IllegalArgumentException(_label + " has no choice '" + value + "'");
                return choice.getValue();
            }
        }
    }

    public static final class Builder {
        private final String _id;
        private final Kind _kind;
        private final Type _type;
        private final String _label;
        private String _help = "";
        private Set<Scope> _scopes = EnumSet.allOf(Scope.class);
        private Predicate<SwccgFormat> _available = f -> true;
        private Function<SwccgFormat, String> _default = f -> null;
        private Function<SwccgFormat, String> _defaultLabel;
        private ToIntFunction<SwccgFormat> _min = f -> 0;
        private ToIntFunction<SwccgFormat> _max = f -> 0;
        private final List<Choice> _choices = new ArrayList<>();
        private Function<String, String> _shortLabel;

        private Builder(String id, Kind kind, Type type, String label) {
            _id = id;
            _kind = kind;
            _type = type;
            _label = label;
        }

        public Builder help(String help) { _help = help; return this; }
        public Builder scopes(Scope first, Scope... rest) { _scopes = EnumSet.of(first, rest); return this; }
        public Builder availableWhen(Predicate<SwccgFormat> available) { _available = available; return this; }
        public Builder defaultValue(Function<SwccgFormat, String> def) { _default = def; return this; }
        public Builder defaultLabel(Function<SwccgFormat, String> label) { _defaultLabel = label; return this; }
        public Builder range(ToIntFunction<SwccgFormat> min, ToIntFunction<SwccgFormat> max) { _min = min; _max = max; return this; }
        public Builder choice(String value, String label, String shortLabel) { _choices.add(new Choice(value, label, shortLabel)); return this; }
        public Builder shortLabel(Function<String, String> shortLabel) { _shortLabel = shortLabel; return this; }

        public GameOption build() {
            return new GameOption(this);
        }
    }
}
