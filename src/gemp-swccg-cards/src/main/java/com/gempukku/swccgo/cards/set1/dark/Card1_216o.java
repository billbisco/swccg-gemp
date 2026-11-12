package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Fear Will Keep Them In Line
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (December 1995) text, from wiki.swccg.com [[Fear Will Keep Them In Line]] (Decipher archive scan).
 * As printed the starship must be at a system you control (sectors came later). Decipher's Glossary Version 2.0
 * (November 1998, [[Fear Will Keep Them In Line (Errata)]]) added "or sector", which is the current card (Card1_216).
 */
public class Card1_216o extends Card1_216 {
    public Card1_216o() {
        super();
        setGameText("Deploy on any capital starship. When that starship is at a system you control, your total power is +1 in battles at related sites.");
        setTextState(new TextState("1_216", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected Filter getControlledLocation() {
        return Filters.system;
    }
}
