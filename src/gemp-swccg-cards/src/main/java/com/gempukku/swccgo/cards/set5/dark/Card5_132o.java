package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Blasted Droid
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Blasted Droid]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), whose text the current card keeps.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_132o extends Card5_132 {
    public Card5_132o() {
        super();
        setGameText("During your control phase, fire (for free) one of your blasters carried by a trooper or one of your automated weapons. Hit target is immediately lost.");
        setTextState(new TextState("5_132", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
