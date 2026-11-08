package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Imperial Tyranny (V)
 * Original version of 1230_45: the Virtual Block 3 release sheet text (Dec 2009), "▼ Empire's New Order" and "on top of Reserve Deck".
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_045 extends Card1230_045 {
    public Card2230_045() {
        super();
        setGameText("USED: Deploy Empire's New Order from Reserve Deck; reshuffle. OR If Vader just won a battle at the same site as an alien, place that alien out of play. LOST: If a battle was just initiated where you have a character weapon card, place the top card of your Lost Pile on top of Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1230_45", "",
                "Virtual Block 3 release sheet (2009-12-07), card #44", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
