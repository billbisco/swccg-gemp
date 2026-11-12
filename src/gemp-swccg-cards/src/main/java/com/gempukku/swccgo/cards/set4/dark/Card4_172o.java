package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: TIE Avenger
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Boosted TIE Cannon may deploy here" plays as "aboard"; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card4_172o extends Card4_172 {
    public Card4_172o() {
        super();
        setGameText("Deploy -1 to same system as any Imperial capital starship. May add 1 pilot. Boosted TIE Cannon may deploy here.");
        setTextState(new TextState("4_172", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }
}
