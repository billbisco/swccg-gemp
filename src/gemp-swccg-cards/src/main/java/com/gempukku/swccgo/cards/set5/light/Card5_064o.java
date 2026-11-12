package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Punch It!
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Punch It!]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_064o extends Card5_064 {
    public Card5_064o() {
        super();
        setGameText("If Han or your Lando is piloting a starfighter which is defending a battle at a system, add one battle destiny (add two if starfighter is Millennium Falcon). Also, starfighter is immune to attrition for remainder of turn.");
        setTextState(new TextState("5_64", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 1, 1)));
    }
}
