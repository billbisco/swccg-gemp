package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Snowspeeder
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "May move as a 'react' only to Hoth sites" plays as the current "to Hoth sites"; Decipher's Glossary Supplement (2002) only reworded it.
 */
public class Card3_069o extends Card3_069 {
    public Card3_069o() {
        super();
        setGameText("May add 1 pilot or passenger. Permanent pilot aboard provides ability of 1. May move as a 'react' only to Hoth sites.");
        setTextState(new TextState("3_69", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(2002, 1, 29)));
    }
}
