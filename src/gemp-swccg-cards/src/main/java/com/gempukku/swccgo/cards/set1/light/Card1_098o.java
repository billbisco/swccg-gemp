package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Narrow Escape
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (December 1995) text, from wiki.swccg.com [[Narrow Escape]] (Decipher archive scan). As printed it
 * cancels the battle by moving all your characters there away. Decipher's Glossary Supplement (29 January 2002,
 * [[Narrow Escape (Errata)]], Card1_098d) moved all your cards with ability and kept the cancel; the current card
 * (Card1_098) only moves them away.
 */
public class Card1_098o extends Card1_098 {
    public Card1_098o() {
        super();
        setGameText("Cancel a battle just initiated at a site where any Rebel of ability > 2 is present, by moving all your characters there away (at normal use of the Force).");
        setTextState(new TextState("1_98", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected Filter getCardsToMoveAway() {
        return Filters.character;
    }

    @Override
    protected boolean cancelsBattle() {
        return true;
    }
}
