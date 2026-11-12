package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: IG-2000
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Ion Cannon may deploy here" plays as "aboard"; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card4_169o extends Card4_169 {
    public Card4_169o() {
        super();
        setGameText("May add 1 pilot (must be a smuggler or bounty hunter) and 2 passengers. Maneuver +3 and immune to attrition < 3 if IG-88 piloting. Ion Cannon may deploy here.");
        setTextState(new TextState("4_169", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }
}
