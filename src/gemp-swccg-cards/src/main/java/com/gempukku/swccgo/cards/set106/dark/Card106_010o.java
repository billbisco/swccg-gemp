package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black Squadron TIE
 * Text state: o (as printed, May 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Supplement (29 January 2002) reworded the deploy clause to "Deploy -2 to Death Star or same location as Vader" (current text). The wording change does not change how the card plays, so this state reuses the current card's
 * (Card106_010) game text code and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[Black Squadron TIE]].
 */
public class Card106_010o extends Card106_010 {
    public Card106_010o() {
        super();
        setGameText("Deploy -2 to same location as Darth Vader or at Death Star. Permanent pilot aboard provides ability of 2 and adds 2 to power. Adds 1 to its weapon destiny draws.");
        setTextState(new TextState("106_10", "Printed",
                "Official Tournament Sealed Deck printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2002, 1, 29)));
    }
}
