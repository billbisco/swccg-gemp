package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Lt. Pol Treidum
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Lt. Pol Treidum]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), wiki [[Lt. Pol Treidum (Errata)]].
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_095) and differs only in its game text and text-state information.
 */
public class Card2_095o extends Card2_095 {
    public Card2_095o() {
        super();
        setGameText("While at Docking Control Room 327, adds 2 to your total power at Docking Bay 327. Once per turn during your control phase, if at same site as an Undercover spy, may draw destiny. Spy's \"cover is broken\" if destiny = spy's ability.");
        setTextState(new TextState("2_95", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
