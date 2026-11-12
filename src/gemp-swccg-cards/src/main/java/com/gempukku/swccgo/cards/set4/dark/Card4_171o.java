package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Punishing One
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Boosted TIE Cannon may deploy here" plays as "aboard"; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card4_171o extends Card4_171 {
    public Card4_171o() {
        super();
        setGameText("May add 1 pilot (must be a smuggler or bounty hunter) and 1 passenger. Immune to attrition < 3 if Dengar piloting. Deploy -3 when deploying with Dengar. Boosted TIE Cannon may deploy here.");
        setTextState(new TextState("4_171", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }
}
