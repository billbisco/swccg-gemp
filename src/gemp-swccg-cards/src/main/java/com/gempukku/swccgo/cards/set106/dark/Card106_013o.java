package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Starship
 * Subtype: Capital
 * Title: Dreadnaught-Class Heavy Cruiser
 * Text state: o (as printed, May 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Version 2.0 (November 1998) explains that 'here' on these starships means "on this card" and
 * rewords it to 'aboard' (current text). The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[Dreadnaught-Class Heavy Cruiser]].
 */
public class Card106_013o extends Card106_013 {
    public Card106_013o() {
        super();
        setGameText("May add 3 pilots, 6 passengers and 4 TIEs. Has ship-docking capability. Permanent pilots aboard provide total ability of 2. Turbolaser Battery may deploy here. Your TIEs present are each power +1.");
        setTextState(new TextState("106_13", "Printed",
                "Official Tournament Sealed Deck printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }
}
