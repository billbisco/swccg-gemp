package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: The Emperor's Prize
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[The Emperor's Prize]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_124o extends Card5_124 {
    public Card5_124o() {
        super();
        setGameText("If Luke was just 'frozen,' deploy on Emperor Palpatine or Detention Block Corridor. Target Luke and Vader. When reached by targets, place Utinni Effect on Luke and opponent must lose half of Life Force (round down). If Luke released, lose Utinni Effect. (Immune to Alter.)");
        setTextState(new TextState("5_124", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 1, 1)));
    }
}
