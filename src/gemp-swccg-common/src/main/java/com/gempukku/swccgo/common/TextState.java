package com.gempukku.swccgo.common;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An older text state of one printing (docs/legacy-versions.md, "Text states"; ai-team-skills
 * shared/swccg-card-identity, "Text states of one printing (errata)").
 * <p>
 * The bare id ({@code 2_45}) is the card's current text. A text state appends a letter to the card number:
 * <ul>
 *   <li>{@code o} = the text as first published (print), e.g. {@code 2_45o};</li>
 *   <li>{@code d} = Decipher's last word (1998 Glossary / 2002 Supplement), e.g. {@code 2_45d};</li>
 *   <li>{@code o1}, {@code o2} … {@code o9} = intermediate revisions in date order, only when a printing has more
 *       than three states.</li>
 * </ul>
 * An alternate-image printing keeps its {@code ai} before the state letter ({@code 1250_45aio}). The class of a
 * text state is the bare card's class name with the state appended ({@code Card2_045o}); a state with the same text as
 * another id may instead be an alias line in blueprintMapping.txt.
 * <p>
 * A text state is the same card as its bare printing: same title, uniqueness and persona, and deck counts and bans
 * count it as the bare card. It is legal only in formats that allow text states (Anything Goes).
 *
 * @param currentId    the bare id of the card this is a text state of (e.g. "2_45")
 * @param label        short visible label: "Printed" for {@code o}, "D-E1" for Decipher's errata, …
 * @param source       where the text comes from, e.g. "Decipher Glossary Version 2.0"
 * @param printedOn    date the text was published
 * @param supersededOn date the text was replaced; may be an upper bound, null if unknown
 */
public record TextState(String currentId, String label, String source, LocalDate printedOn, LocalDate supersededOn) {

    /** State letter of the text as first published. */
    public static final String ORIGINAL = "o";
    /** State letter of Decipher's last word (1998 Glossary / 2002 Supplement). */
    public static final String DECIPHER = "d";

    // card number (with an optional alternate-image "ai") followed by the state letter(s)
    private static final Pattern CARD_NUMBER = Pattern.compile("^(\\d+(?:ai)?)(o[1-9]?|d)$");

    public TextState {
        if (currentId == null || label == null || label.isEmpty() || source == null || printedOn == null)
            throw new IllegalArgumentException("A text state needs its current id, a label, a source and a date");
        if (isTextStateBlueprint(currentId))
            throw new IllegalArgumentException("The current id of a text state is a bare id: " + currentId);
    }

    /** True when the blueprint id (modifiers * ^ and _BACK allowed) is a text state, e.g. "2_45d" or "2_45o*". */
    public static boolean isTextStateBlueprint(String blueprintId) {
        return stateOf(blueprintId) != null;
    }

    /** The state suffix of a text-state id ("2_45d" -> "d", "1_19o1" -> "o1"), or null if the id is not one. */
    public static String stateOf(String blueprintId) {
        Matcher m = cardNumberMatcher(blueprintId);
        return m != null ? m.group(2) : null;
    }

    /**
     * The bare id of the card a text state belongs to ("2_45d" -> "2_45", "1250_45aio" -> "1250_45ai",
     * "2_45o_BACK" -> "2_45_BACK"), or null if the id is not a text state. Modifiers * and ^ are dropped.
     */
    public static String currentIdOf(String blueprintId) {
        Matcher m = cardNumberMatcher(blueprintId);
        if (m == null)
            return null;
        String[] parts = strip(blueprintId).split("_", 3);
        String current = parts[0] + "_" + m.group(1);
        return parts.length > 2 ? current + "_" + parts[2] : current;
    }

    /** The id of a state of a bare printing ("2_45", "d" -> "2_45d"; "2_45_BACK", "o" -> "2_45o_BACK"). */
    public static String textStateIdOf(String currentId, String state) {
        if (!state.matches("o[1-9]?|d"))
            throw new IllegalArgumentException("Not a text state: " + state);
        String[] parts = strip(currentId).split("_", 3);
        String id = parts[0] + "_" + parts[1] + state;
        return parts.length > 2 ? id + "_" + parts[2] : id;
    }

    /**
     * The class-name card number for a card-number part of a blueprint id, padded to three digits and keeping the
     * "ai" and text-state suffixes ("45d" -> "045d", "45aio1" -> "045aio1"), or null if the part has no text state.
     */
    public static String paddedCardNumber(String cardNumber) {
        Matcher m = CARD_NUMBER.matcher(cardNumber);
        if (!m.matches())
            return null;
        String number = m.group(1);
        String ai = number.endsWith("ai") ? "ai" : "";
        int n = Integer.parseInt(number.substring(0, number.length() - ai.length()));
        return String.format("%03d", n) + ai + m.group(2);
    }

    /** All state suffixes, in the order the card pool lists them. */
    public static String[] allStates() {
        return new String[]{ORIGINAL, "o1", "o2", "o3", "o4", "o5", "o6", "o7", "o8", "o9", DECIPHER};
    }

    private static Matcher cardNumberMatcher(String blueprintId) {
        if (blueprintId == null)
            return null;
        String[] parts = strip(blueprintId).split("_", 3);
        if (parts.length < 2 || !parts[0].chars().allMatch(Character::isDigit) || parts[0].isEmpty())
            return null;
        Matcher m = CARD_NUMBER.matcher(parts[1]);
        return m.matches() ? m : null;
    }

    private static String strip(String blueprintId) {
        return blueprintId.replace("*", "").replace("^", "");
    }
}
