package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.common.Side;

import java.util.Locale;

/** The side the player takes in a Play now game. A guest's seat is named after it. */
public enum PlayNowSide {
    LIGHT("light", Side.LIGHT, "Light Side", "/guest/panh-precon-ls.txt", "P-ANH Light Side precon"),
    DARK("dark", Side.DARK, "Dark Side", "/guest/panh-precon-ds.txt", "P-ANH Dark Side precon");

    private final String _code;
    private final Side _side;
    private final String _seatName;
    private final String _deckResource;
    private final String _deckName;

    PlayNowSide(String code, Side side, String seatName, String deckResource, String deckName) {
        _code = code;
        _side = side;
        _seatName = seatName;
        _deckResource = deckResource;
        _deckName = deckName;
    }

    public String getCode() {
        return _code;
    }

    public Side getSide() {
        return _side;
    }

    /**
     * A guest's participant id in the game: "Light Side" or "Dark Side". It only has to be unique within one game.
     * A space alone does not keep accounts off it: the site's names allow spaces, and "Light_Side" fits Play's
     * 10 characters and reads the same. Both spellings of both names are reserved instead, on Play
     * ({@link #isSeatName}) and in the sign-in service (dests by Bill, 2026-11-11).
     */
    public String getSeatName() {
        return _seatName;
    }

    String getDeckResource() {
        return _deckResource;
    }

    public String getDeckName() {
        return _deckName;
    }

    /**
     * Whether a name is a reserved guest seat name: "Light Side", "Light_Side", "Dark Side" or "Dark_Side", case
     * ignored. A space and an underscore are different names on the site (Bill, 2026-11-11, t121u); both spellings
     * of the seat names are reserved. "Light Side 2" and the bare words are not.
     */
    public static boolean isSeatName(String name) {
        if (name == null)
            return false;
        String lower = name.trim().toLowerCase(Locale.ROOT);
        for (PlayNowSide side : values()) {
            String seat = side._seatName.toLowerCase(Locale.ROOT);
            if (seat.equals(lower) || seat.replace(' ', '_').equals(lower))
                return true;
        }
        return false;
    }

    public PlayNowSide opposite() {
        return this == LIGHT ? DARK : LIGHT;
    }

    /** @return the side, or null if the code is not "light" or "dark" */
    /** The side whose bundled deck has this name, or null. */
    public static PlayNowSide fromDeckName(String deckName) {
        for (PlayNowSide side : values()) {
            if (side.getDeckName().equals(deckName)) {
                return side;
            }
        }
        return null;
    }

    public static PlayNowSide fromCode(String code) {
        if (code == null) {
            return null;
        }
        String c = code.trim().toLowerCase(Locale.ROOT);
        for (PlayNowSide side : values()) {
            if (side._code.equals(c)) {
                return side;
            }
        }
        return null;
    }
}
