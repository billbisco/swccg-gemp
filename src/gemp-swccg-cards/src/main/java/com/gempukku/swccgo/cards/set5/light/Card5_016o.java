package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Advantage
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Advantage]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_016o extends Card5_016 {
    public Card5_016o() {
        super();
        setGameText("If you just won a battle or duel where an opponent's character of ability > 3 was lost, deploy on one of your warriors involved. Warrior is power +2. During each of opponent's move phases, opponent loses 1 Force (2 if character was a Dark Jedi). (Immune to Control.)");
        setTextState(new TextState("5_16", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 1, 1)));
    }
}
