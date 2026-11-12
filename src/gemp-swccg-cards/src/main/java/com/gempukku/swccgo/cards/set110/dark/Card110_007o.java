package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Character
 * Subtype: Alien
 * Title: Dengar With Blaster Carbine
 * Text state: o (as printed, January 2000; docs/legacy-versions.md "Text states")
 * <p>
 * The Decipher Glossary Supplement (29 January 2002) adds "draw destiny;" as a rewording already in force (marked §):
 * the printed weapon already draws destiny. The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[Dengar With Blaster Carbine]].
 */
public class Card110_007o extends Card110_007 {
    public Card110_007o() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Permanent weapon is •Dengar's Blaster Carbine (may target a character, creature or vehicle for free; target hit, and its forfeit = 0, if destiny +1 > defense value; may be fired twice per battle).");
        setTextState(new TextState("110_7", "Printed",
                "Enhanced Jabba's Palace printing (Decipher, January 2000)",
                LocalDate.of(2000, 1, 1), LocalDate.of(2002, 1, 29)));
    }
}
