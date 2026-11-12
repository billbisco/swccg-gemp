package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Imperial Decree
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Imperial Decree]] (Decipher archive scan); superseded by the Decipher Glossary Supplement (29 January 2002), whose text the current card keeps.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card5_120o extends Card5_120 {
    public Card5_120o() {
        super();
        setGameText("Deploy on your side of table. Whenever you control any two Rebel Base locations, or any one planet site and two systems, the effects of Revolution and all opponent's Force drain bonuses everywhere are ignored. (Immune to Alter.)");
        setTextState(new TextState("5_120", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2002, 1, 29)));
    }
}
