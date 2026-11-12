package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Major Bren Derlin
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: The printed clauses are the current ones in another order; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_013o extends Card3_013 {
    public Card3_013o() {
        super();
        setGameText("While on Hoth, may use 1 Force to cancel Breached Defenses and opponent may not 'react' to any Echo site. While at Cantina, power +1 and may use 1 Force to cancel Local Trouble.");
        setTextState(new TextState("3_13", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
