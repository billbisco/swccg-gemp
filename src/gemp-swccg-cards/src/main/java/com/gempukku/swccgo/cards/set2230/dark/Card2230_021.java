package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Wooof (V)
 * Original version of 1230_21: the Virtual Block 3 release sheet text (Dec 2009), "may ▼ Jabba aboard" (the card list reads "one Jabba").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_021 extends Card1230_021 {
    public Card2230_021() {
        super();
        setGameText("Adds 3 to power of anything he pilots. Wooof may deploy with any cruiser using Combat Response. When deployed, may draw top card from Reserve Deck. During your deploy phase, if piloting Jabba's Space Cruiser, may deploy Jabba aboard (for free) from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1230_21", "",
                "Virtual Block 3 release sheet (2009-12-07), card #21", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
