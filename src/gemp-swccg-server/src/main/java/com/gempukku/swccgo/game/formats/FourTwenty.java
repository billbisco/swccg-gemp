package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.SwccgCardBlueprint;

import java.util.EnumSet;
import java.util.Set;

/**
 * The "Deck: 420" deck rule (swccg-guild/work#224), created by Clayton Moore (cmoore89): every card must have printed
 * destiny 0, 2 or 4, and Objectives are not allowed. Bill (2026-11): locations count as destiny 0 even with no printed
 * number; every other card needs a printed 0, 2 or 4. Only the card's printed (base) destiny counts: in-game modifiers
 * and conditional destiny bonuses never qualify a card.
 */
public final class FourTwenty {
    /** The value of a printed π destiny, as card blueprints store it. */
    private static final float PI = 3.1415927f;

    /**
     * Card categories whose blueprint destiny is an engine default, not a number printed on the card. Defensive Shields
     * print no destiny number (GEMP gives them 0 so the engine has a value), so they do not qualify.
     */
    private static final Set<CardCategory> NO_PRINTED_DESTINY = EnumSet.of(CardCategory.DEFENSIVE_SHIELD);

    private FourTwenty() {
    }

    /**
     * Why a card fails 420, or {@code null} when it is allowed. The reason names the restriction it failed.
     */
    public static String violation(SwccgCardBlueprint card) {
        if (card == null)
            return null;
        CardCategory category = card.getCardCategory();
        if (category == CardCategory.OBJECTIVE)
            return "Objectives are not permitted";
        if (category == CardCategory.LOCATION)
            return null; // rules-defined destiny 0, printed or not
        Float destiny = card.getDestiny();
        if (destiny == null || NO_PRINTED_DESTINY.contains(category))
            return "it has no printed destiny number; only 0, 2 or 4 is permitted";
        if (Math.abs(destiny - PI) < 0.0001f)
            return "its printed destiny is π, not 0, 2 or 4";
        if (destiny != Math.rint(destiny))
            return "its printed destiny " + destiny + " is not 0, 2 or 4";
        int value = Math.round(destiny);
        if (value != 0 && value != 2 && value != 4)
            return "its printed destiny " + value + " is not 0, 2 or 4";
        return null;
    }
}
