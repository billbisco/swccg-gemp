package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Knowledge And Defense
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: The printed text is the current GEMP text without the quotes around Insert; Decipher's Glossary Supplement (2002) reworded ", but" as "and" (state d, Card4_125d).
 */
public class Card4_125o extends Card4_125 {
    public Card4_125o() {
        super();
        setGameText("Insert in opponent's Reserve Deck. When Effect reaches top it is lost, but opponent may not initiate any battles for remainder of turn. (Immune to Alter.)");
        setTextState(new TextState("4_125", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2002, 1, 29)));
    }
}
