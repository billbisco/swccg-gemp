package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_061;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Weapon
 * Title: Feltipern Trevagg's Stun Rifle (V)
 * Original version of 1230_61: the Virtual Block 3 release sheet text (Dec 2009), "and may take one Interrupt" (the card list added "you").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_061 extends Card1230_061 {
    public Card2230_061() {
        super();
        setGameText("Use 1 Force to deploy on your bounty hunter (free on Feltipern Trevagg). May target a character for free. Draw destiny. Target captured (and may take one Interrupt from Lost Pile into hand) if destiny +2 > defense value.");
        setLegacyVersion(new LegacyVersion("1230_61", "",
                "Virtual Block 3 release sheet (2009-12-07), card #60", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
