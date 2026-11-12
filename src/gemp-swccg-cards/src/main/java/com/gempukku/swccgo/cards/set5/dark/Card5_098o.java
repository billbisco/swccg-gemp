package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Imperial Trooper Guard Dainsom
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Imperial Trooper Guard Dainsom]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_098o extends Card5_098 {
    public Card5_098o() {
        super();
        setGameText("Deploys only aboard Executor or on any Death Star site. Cannot move. May cancel opponent's Force drains at adjacent sites.");
        setTextState(new TextState("5_98", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 1, 1)));
    }
}
