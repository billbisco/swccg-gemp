package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Echo Trooper Backpack
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "any trooper" plays as "your trooper" (a device deploys on your own cards unless it says otherwise) and "may carry and use" any number of weapons and devices is the current "may use"; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_029o extends Card3_029 {
    public Card3_029o() {
        super();
        setGameText("Deploy on any trooper. May carry and use any number of weapons and devices. Trooper is immune to attrition < 3 when at a planet site.");
        setTextState(new TextState("3_29", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
