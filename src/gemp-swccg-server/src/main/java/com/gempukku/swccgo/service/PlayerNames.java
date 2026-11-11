package com.gempukku.swccgo.service;

import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.guest.PlayNowSide;

import java.text.Normalizer;
import java.util.Locale;

/**
 * The site's rule for a <em>new</em> player name on Play (classic signup, the sign-in service's signup and rename),
 * matching the sign-in service (swccg-guild/work#249, #260; Bill 2026-11-11 t121u, t122u):
 * <ul>
 * <li>1 to 32 characters.</li>
 * <li>Letters and digits from any writing system (combining marks after them), space, hyphen and underscore.</li>
 * <li>No space or underscore at either end and no two in a row; at least one letter or digit. Case is kept.</li>
 * <li>A space and an underscore are different names. Uniqueness ignores case (checked by the DAO).</li>
 * </ul>
 * Play stores names as 3-byte UTF-8 (utf8mb3) and keeps doing so (Bill 2026-11-11, t124u), so characters outside
 * the Basic Multilingual Plane (above U+FFFF: 4-byte letters, digits, marks, emoji) are refused here, before any
 * database write. The sign-in service strips them from a new name first (t127u), so a refusal here only reaches
 * classic signup and direct callers.
 * Existing names are grandfathered: this is never applied to signing in.
 */
public final class PlayerNames {
    public static final int MIN_LEN = 1;
    public static final int MAX_LEN = 32;
    private static final String[] RESERVED_PREFIXES = {"admin", "guest", "system", "bye"};

    private PlayerNames() {
    }

    /** Whether a name can be issued as written (characters, length, separators). */
    public static boolean isWellFormed(String name) {
        if (name == null || name.isEmpty() || !Normalizer.isNormalized(name, Normalizer.Form.NFC))
            return false;
        int count = 0;
        boolean alnum = false;
        boolean prevSep = true; // so a leading separator is refused
        boolean prevBase = false;
        for (int i = 0; i < name.length(); ) {
            int cp = name.codePointAt(i);
            i += Character.charCount(cp);
            count++;
            if (cp > 0xFFFF)
                return false;
            int type = Character.getType(cp);
            if (cp == ' ' || cp == '_') {
                if (prevSep)
                    return false;
                prevSep = true;
                prevBase = false;
            } else if (cp == '-') {
                prevSep = false;
                prevBase = false;
            } else if (Character.isLetter(cp) || type == Character.DECIMAL_DIGIT_NUMBER) {
                alnum = true;
                prevSep = false;
                prevBase = true;
            } else if ((type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK) && prevBase) {
                prevSep = false;
            } else {
                return false;
            }
        }
        char last = name.charAt(name.length() - 1);
        return alnum && count >= MIN_LEN && count <= MAX_LEN && last != ' ' && last != '_';
    }

    /**
     * Whether a new name is reserved: Play's reserved prefixes (also with spaces, hyphens or underscores inside),
     * Play's bots, and the guest seat names in both spellings.
     */
    public static boolean isReserved(String name) {
        if (name == null)
            return true;
        String lower = name.toLowerCase(Locale.ROOT);
        String squashed = lower.replace(" ", "").replace("-", "").replace("_", "");
        for (String p : RESERVED_PREFIXES)
            if (lower.startsWith(p) || squashed.startsWith(p))
                return true;
        return Player.isBot(name) || PlayNowSide.isSeatName(name);
    }

    /** What {@link #forDisplay} shows when nothing of a name would be left. */
    public static final String DISPLAY_PLACEHOLDER = "(unnamed player)";

    /**
     * A stored name as it may be shown on a page (swccg-guild/work#249, t129u): every character a name can never
     * hold is removed (above U+FFFF, emoji and other symbols, punctuation other than "-" and "_", control and
     * invisible characters, combining marks with no letter or digit before them); other dashes and spaces show as
     * "-" and " ". A safety net for old rows, imports or a future bug; stored data is never changed. It never
     * trims, collapses or cuts and keeps case, so a grandfathered name of letters, digits, spaces, hyphens and
     * underscores shows exactly as stored. A bot's "~" mark stays. Escape the result for HTML as usual.
     * Mirrors the sign-in service's names.Display and js/swccg-names.js.
     */
    public static String forDisplay(String name) {
        if (name == null)
            return DISPLAY_PLACEHOLDER;
        String prefix = "";
        if (Player.isBot(name)) {
            prefix = Player.BOT_NAME_PREFIX;
            name = name.substring(Player.BOT_NAME_PREFIX.length());
        }
        StringBuilder b = new StringBuilder(name.length());
        boolean base = false;
        boolean alnum = false;
        for (int i = 0; i < name.length(); ) {
            int cp = name.codePointAt(i);
            i += Character.charCount(cp);
            int type = Character.getType(cp);
            if (cp > 0xFFFF || type == Character.SURROGATE) {
                base = false;
            } else if (cp == '_' || cp == '-' || cp == ' ') {
                b.appendCodePoint(cp);
                base = false;
            } else if (type == Character.DASH_PUNCTUATION) {
                b.append('-');
                base = false;
            } else if ((Character.isWhitespace(cp) && (cp < 0x1C || cp > 0x1F)) || Character.isSpaceChar(cp) || cp == 0x85) {
                b.append(' ');
                base = false;
            } else if (isInvisible(cp, type)) {
                // dropped: format, bidi, variation selectors, Hangul fillers, ...
            } else if (Character.isLetter(cp) || type == Character.DECIMAL_DIGIT_NUMBER) {
                b.appendCodePoint(cp);
                base = true;
                alnum = true;
            } else if ((type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK) && base) {
                b.appendCodePoint(cp);
            }
        }
        if (!alnum)
            return prefix.isEmpty() ? DISPLAY_PLACEHOLDER : prefix + b;
        return prefix + b;
    }

    /** Characters that show as nothing: format (Cf), variation selectors and the other default-ignorable ones. */
    private static boolean isInvisible(int cp, int type) {
        return type == Character.FORMAT
                || (cp >= 0xFE00 && cp <= 0xFE0F) || (cp >= 0x180B && cp <= 0x180F)
                || cp == 0x034F || cp == 0x115F || cp == 0x1160 || cp == 0x17B4 || cp == 0x17B5
                || cp == 0x3164 || cp == 0xFFA0 || (cp >= 0x2060 && cp <= 0x206F) || (cp >= 0xFFF0 && cp <= 0xFFF8);
    }

    /** Whether a name may be taken as a new name (well formed and not reserved); uniqueness is separate. */
    public static boolean isAllowedNewName(String name) {
        return isWellFormed(name) && !isReserved(name);
    }
}
