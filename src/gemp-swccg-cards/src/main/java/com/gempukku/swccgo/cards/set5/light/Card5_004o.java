package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Kebyc
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Kebyc]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_004o extends Card5_004 {
    public Card5_004o() {
        super();
        setGameText("When at a site and opponent is losing Force from Force drains at cloud sectors on same planet, lost Force must come from Reserve Deck if possible.");
        setTextState(new TextState("5_4", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 1, 1)));
    }
}
