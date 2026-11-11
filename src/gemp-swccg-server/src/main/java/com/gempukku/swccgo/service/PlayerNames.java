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
 * Play stores names as 3-byte UTF-8, so characters outside the Basic Multilingual Plane are refused here.
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

    /** Whether a name may be taken as a new name (well formed and not reserved); uniqueness is separate. */
    public static boolean isAllowedNewName(String name) {
        return isWellFormed(name) && !isReserved(name);
    }
}
