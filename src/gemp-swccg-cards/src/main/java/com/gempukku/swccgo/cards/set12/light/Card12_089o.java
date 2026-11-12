package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Coruscant
 * Type: Objective
 * Title: The Hyperdrive Generator's Gone / We'll Need A New One
 * Text state: o (as printed, August 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed 0 side from wiki.swccg.com [[The Hyperdrive Generator's Gone / We'll Need A New One]] (Decipher archive
 * scan), the same words as the current card (Card12_089): this state plays as the current card. The errata is on the
 * 7 side: see Card12_089o_BACK.
 */
public class Card12_089o extends Card12_089 {
    public Card12_089o() {
        super();
        setTextState(new TextState("12_89", "Printed", "Coruscant printing (Decipher, August 2001)",
                LocalDate.of(2001, 8, 1), LocalDate.of(2023, 7, 30)));
    }
}
