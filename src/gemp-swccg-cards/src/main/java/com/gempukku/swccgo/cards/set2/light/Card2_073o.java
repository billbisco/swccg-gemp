package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Tantive IV
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Tantive IV]] (Decipher archive scan); superseded by Decipher errata, wiki [[Tantive IV (Errata)]] (Glossary; upper bound November 1998).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_073) and differs only in its game text and text-state information.
 */
public class Card2_073o extends Card2_073 {
    public Card2_073o() {
        super();
        setGameText("May add 3 pilots and 4 passengers. Has ship-docking capability. Permanent pilot aboard provides ability of 2. May be deployed to any system (even without presence or Force icons).");
        setTextState(new TextState("2_73", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
