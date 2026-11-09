package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_177;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Character
 * Subtype: Droid
 * Title: 4-LOM With Concussion Rifle (V)
 * Original version of 1262_1 (current class 601_177): the Virtual Block 6.2 release sheet text ("target is power -1",
 * no duration), also on the PC card image and the Master List (2013-04-27). Replaced on 2014-03-27 (PC forum "Current
 * Rulings/Errata": "until end of turn" added "in order to prevent a duration issue"). Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2262_001 extends Card601_177 {
    public Card2262_001() {
        super();
        setGameText("Permanent weapon is •4-LOM's Concussion Rifle (may target a character for free; target is power -1). Once per turn, may place a card from hand on bottom of Used Pile to draw top card from Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1262_1", "",
                "Virtual Block 6.2 release sheet (2009-12-07), card #2-01", LocalDate.of(2009, 12, 7), LocalDate.of(2014, 3, 27)));
    }

    @Override
    protected boolean powerMinusOneUntilEndOfTurn() {
        return false;
    }
}
