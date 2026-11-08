package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Tusken Breath Mask (V)
 * Original version of 1240_92: the Virtual Block 4 release sheet text (Dec 2009), "may add X to your ability during battle" (the card list reads "total ability").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_092 extends Card1240_092 {
    public Card2240_092() {
        super();
        setGameText("Deploy on an exterior Tatooine battleground site. X = the face up destiny number of your Objective. Once per turn, if all your ability on table is provided by aliens, may add X to your ability during battle at a site. This Effect canceled if opponent controls this site.");
        setLegacyVersion(new LegacyVersion("1240_92", "",
                "Virtual Block 4 release sheet (2009-12-07), card #90E", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
