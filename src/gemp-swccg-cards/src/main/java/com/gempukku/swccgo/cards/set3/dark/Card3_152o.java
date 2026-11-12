package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Stalker
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Related sites at same system" and "sites related to same system" are the same; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_152o extends Card3_152 {
    public Card3_152o() {
        super();
        setGameText("May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Has ship-docking capability. Permanent pilot aboard provides ability of 1. Probe Droids deploy free to related sites at same system.");
        setTextState(new TextState("3_152", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
