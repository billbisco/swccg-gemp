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
     * A guest's participant id in the game: "Light Side" or "Dark Side". It has a space, so no account can have it,
     * and it only has to be unique within one game.
     */
    public String getSeatName() {
        return _seatName;
    }

    String getDeckResource() {
        return _deckResource;
    }

    String getDeckName() {
        return _deckName;
    }

    public PlayNowSide opposite() {
        return this == LIGHT ? DARK : LIGHT;
    }

    /** @return the side, or null if the code is not "light" or "dark" */
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
