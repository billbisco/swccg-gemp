package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: I've Got A Problem Here
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (December 1995) text, from wiki.swccg.com [[I've Got A Problem Here]] (Decipher archive scan). As
 * printed it plays only where a battle just finished at a system (sectors came later). Decipher's Glossary Version 2.0
 * (November 1998) and Supplement (29 January 2002, [[I've Got A Problem Here (Errata)]]) added "or sector" and
 * "with maneuver"; that is the current card (Card1_253). "With maneuver" only spells out what "destiny > maneuver"
 * needs, so the starfighter must still have maneuver here.
 */
public class Card1_253o extends Card1_253 {
    public Card1_253o() {
        super();
        setGameText("Use 1 Force to target an opponent's starfighter at a system where a battle just finished. Draw destiny. Starfighter immediately lost if destiny > maneuver.");
        setTextState(new TextState("1_253", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected Filter getBattleLocation() {
        return Filters.system;
    }
}
