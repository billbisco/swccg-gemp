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
 * Which characters are letters, digits, combining marks, spaces, dashes or invisible comes from one table pinned to
 * Unicode 15.0 ({@link PlayerNameChars}, t130u), not from {@link Character}: the sign-in service's Go and browsers
 * ship newer Unicode tables than Java 21, and the same table is in the sign-in service and js/swccg-names.js, so a
 * name is read the same way everywhere. Characters added after 15.0 are refused and not shown, like 4-byte ones.
 * Existing names are grandfathered: this is never applied to signing in.
 */
public final class PlayerNames {
    public static final int MIN_LEN = 1;
    public static final int MAX_LEN = 32;
    private static final String[] RESERVED_PREFIXES = {"admin", "guest", "system", "bye"};

    private PlayerNames() {
    }

    /** The Unicode version of the name table. */
    public static final String UNICODE_VERSION = "15.0.0";

    /** Classes in the name table (see {@link PlayerNameChars}). */
    static final char DASH = 'D', SPACE = 'S', INVISIBLE = 'I', ALNUM = 'A', MARK = 'M', OTHER = 'X';

    private static final int[] RUN_START;
    private static final char[] RUN_CLASS;

    static {
        String runs = PlayerNameChars.RUNS;
        int n = runs.length() / 5;
        RUN_START = new int[n];
        RUN_CLASS = new char[n];
        for (int i = 0; i < n; i++) {
            RUN_START[i] = Integer.parseInt(runs.substring(i * 5, i * 5 + 4), 16);
            RUN_CLASS[i] = runs.charAt(i * 5 + 4);
        }
    }

    /** The encoded name table, so tests can check it is the same table as js/swccg-names.js. */
    public static String charTable() {
        return PlayerNameChars.RUNS;
    }

    /** A code point's class in the name table; everything above U+FFFF is {@link #OTHER}. */
    static char classOf(int cp) {
        if (cp < 0 || cp > 0xFFFF)
            return OTHER;
        int lo = 0, hi = RUN_START.length - 1;
        while (lo < hi) {
            int mid = (lo + hi + 1) >>> 1;
            if (RUN_START[mid] <= cp)
                lo = mid;
            else
                hi = mid - 1;
        }
        return RUN_CLASS[lo];
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
            char k = classOf(cp);
            if (cp == ' ' || cp == '_') {
                if (prevSep)
                    return false;
                prevSep = true;
                prevBase = false;
            } else if (cp == '-') {
                prevSep = false;
                prevBase = false;
            } else if (k == ALNUM) {
                alnum = true;
                prevSep = false;
                prevBase = true;
            } else if (k == MARK && prevBase) {
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
     * invisible characters, combining marks with no letter or digit before them, characters added after Unicode 15.0); other dashes and spaces show as
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
            char k = classOf(cp);
            if (cp > 0xFFFF || (cp >= 0xD800 && cp <= 0xDFFF)) {
                base = false;
            } else if (cp == '_' || cp == '-' || cp == ' ') {
                b.appendCodePoint(cp);
                base = false;
            } else if (k == DASH) {
                b.append('-');
                base = false;
            } else if (k == SPACE) {
                b.append(' ');
                base = false;
            } else if (k == INVISIBLE) {
                // dropped: format, bidi, variation selectors, Hangul fillers, ...
            } else if (k == ALNUM) {
                b.appendCodePoint(cp);
                base = true;
                alnum = true;
            } else if (k == MARK && base) {
                b.appendCodePoint(cp);
            }
        }
        if (!alnum)
            return prefix.isEmpty() ? DISPLAY_PLACEHOLDER : prefix + b;
        return prefix + b;
    }

    /** Whether a name may be taken as a new name (well formed and not reserved); uniqueness is separate. */
    public static boolean isAllowedNewName(String name) {
        return isWellFormed(name) && !isReserved(name);
    }
}
