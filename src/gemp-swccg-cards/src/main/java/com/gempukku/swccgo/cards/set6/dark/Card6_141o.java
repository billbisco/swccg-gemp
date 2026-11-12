package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Jabba's Palace
 * Title: All Wrapped Up
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[All Wrapped Up]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card6_141o extends Card6_141 {
    public Card6_141o() {
        super();
        setGameText("Deploy on your side of table. We Have A Prisoner and Oo-ta Goo-ta, Solo? play for free and are immune to Sense. Also, whenever opponent forfeits a character, your bounty hunter present may capture that character. (Immune to Alter.)");
        setTextState(new TextState("6_141", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)));
    }
}
