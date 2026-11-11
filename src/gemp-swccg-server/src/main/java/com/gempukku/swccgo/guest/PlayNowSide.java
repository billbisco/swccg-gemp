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
     * A space alone does not keep accounts off it: the site's names allow spaces and treat a space and an underscore
     * as the same, so "Light_Side" is the same name and fits Play's 10 characters. Both names are reserved instead,
     * on Play ({@link #isSeatName}) and in the sign-in service (dest by Bill, 2026-11-11).
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
     * Whether a name is one of the guest seat names in any spelling that is the same name: case ignored, an
     * underscore counts as a space, runs of spaces count as one. "Light Side 2" and the bare words are not.
     */
    public static boolean isSeatName(String name) {
        if (name == null)
            return false;
        String key = String.join(" ", name.replace('_', ' ').trim().toLowerCase(Locale.ROOT).split("\\s+"));
        for (PlayNowSide side : values())
            if (side._seatName.toLowerCase(Locale.ROOT).equals(key))
                return true;
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
