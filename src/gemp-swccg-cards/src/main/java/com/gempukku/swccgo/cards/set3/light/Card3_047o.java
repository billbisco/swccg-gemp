package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: One More Pass
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Add ability of pilot to Power Harpoon weapon destiny (before weapon destiny is drawn)" is the current text in other words; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_047o extends Card3_047 {
    public Card3_047o() {
        super();
        setGameText("Add ability of pilot to Power Harpoon weapon destiny (before weapon destiny is drawn).");
        setTextState(new TextState("3_47", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
