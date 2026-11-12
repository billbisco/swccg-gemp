package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Obsidian Squadron TIE
 * Text state: o (as printed, May 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Version 2.0 (November 1998) explains that 'here' on these starships means "on this card" and
 * rewords it to 'aboard' (current text). The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[Obsidian Squadron TIE]].
 */
public class Card106_015o extends Card106_015 {
    public Card106_015o() {
        super();
        setGameText("Deploy -1 at any cloud sector. Permanent pilot aboard provides ability of 2 and adds 2 to power. Power +2 at non-unique cloud sectors. Boosted TIE Cannon may deploy here and fires free here.");
        setTextState(new TextState("106_15", "Printed",
                "Official Tournament Sealed Deck printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }
}
