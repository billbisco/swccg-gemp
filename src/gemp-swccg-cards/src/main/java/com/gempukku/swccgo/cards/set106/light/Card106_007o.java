package com.gempukku.swccgo.cards.set106.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red Squadron X-wing
 * Text state: o (as printed, May 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Version 2.0 (November 1998) explains that 'here' on these starships means "on this card" and rewords it to 'aboard' (current text). The wording change does not change how the card plays, so this state reuses the current card's
 * (Card106_007) game text code and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[Red Squadron X-wing]].
 */
public class Card106_007o extends Card106_007 {
    public Card106_007o() {
        super();
        setGameText("Deploy -2 at Yavin 4 or to same location as Red Leader. Permanent pilot aboard provides ability of 2 and adds 2 to power. Proton Torpedoes deploy and fire free here.");
        setTextState(new TextState("106_7", "Printed",
                "Official Tournament Sealed Deck printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }
}
