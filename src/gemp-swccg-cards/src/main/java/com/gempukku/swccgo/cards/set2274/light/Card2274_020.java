package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.cards.set1274.light.Card1274_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: Temporary Foothold
 * Original version of 1274_20: the Virtual Block 7.4 release sheet text (2011-12-16, slip 04; the PC card image and the Master List of
 * 2013-04-27 print it). The PC website card list (by 2012-07-21) writes "draw up to 2 cards" (PC-E1, the current card). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_020 extends Card1274_020 {
    public Card2274_020() {
        super();
        setGameText("Deploy on a battleground. While you occupy at least two battlegrounds, your Force drains here may not be canceled. During battle here or at a related location, may place this Effect in Used Pile to draw up to two cards from Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1274_20", "",
                "Virtual Block 7.4 release sheet (2011-12-16), card #04; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21)));
    }
}
