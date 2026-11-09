package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_079;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Starship
 * Title: Alderaan Consular Ship
 * Original version of 1250_79: the Virtual Block 5 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "initiate a battle" for "initiate battle"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_079 extends Card1250_079 {
    public Card2250_079() {
        super();
        setGameText("May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Opponent must use +2 Force to initiate battle here. Once per game, may deploy a senator aboard from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1250_79", "",
                "Virtual Block 5 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
