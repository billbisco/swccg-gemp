package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_114;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Title: Tydirium (V)
 * Original version of 1230_114: the Virtual Block 3 release sheet text (Dec 2009). The PC website card list (by Jul 2012,
 * PC-E1) is the current text: the card list dropped "here" after "battle damage against you" (the scout must be in the battle to be forfeited either way).
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_114 extends Card1230_114 {
    public Card2230_114() {
        super();
        setGameText("May deploy (with a pilot) without presence or Force icons. May add 2 pilots and 6 passengers. May forfeit a scout aboard to cancel all remaining battle damage against you here.");
        setLegacyVersion(new LegacyVersion("1230_114", "",
                "Virtual Block 3 release sheet (2009-12-07), card #117", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
