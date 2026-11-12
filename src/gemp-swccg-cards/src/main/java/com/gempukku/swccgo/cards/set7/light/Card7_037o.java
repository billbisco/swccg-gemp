package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Droid
 * Title: R3-T2 (Arthree-Teetoo)
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "aboard a capital starship" and "at Central Core"; the Players Committee wrote "your capital starship" and
 * "Death Star: Central Core" (the only Central Core).
 * The bare card (Card7_037) plays "your capital starship"; this state applies aboard any capital starship, as printed.
 */
public class Card7_037o extends Card7_037 {
    public Card7_037o() {
        super();
        setGameText("While aboard a capital starship, adds 1 to power and 2 to hyperspeed, and that starship is immune to attrition < 4. While at Central Core, Death Star requires +2 Force to move.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_37", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }

    /** As printed: "aboard a capital starship" (any player's). */
    @Override
    protected Filter capitalStarshipFilter(PhysicalCard self) {
        return Filters.capital_starship;
    }
}
