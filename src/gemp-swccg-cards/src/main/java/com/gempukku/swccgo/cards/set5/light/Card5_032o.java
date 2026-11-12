package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Armed And Dangerous
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Armed And Dangerous]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_032o extends Card5_032 {
    public Card5_032o() {
        super();
        setGameText("If a battle or duel was just initiated at a site, search your hand or Reserve Deck for one unique weapon matching one of your participating characters. Immediately deploy that weapon (for free) on that character and reshuffle (if from Reserve Deck).");
        setTextState(new TextState("5_32", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 1, 1)));
    }
}
