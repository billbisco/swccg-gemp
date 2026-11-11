package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set1274.dark.Card1274_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Character
 * Title: ••OOM Command Battle Droid
 * PC-E1 version of 1274_2 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2274_2002) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2274_3002), which prints the original words again.
 * Against the original: three changes: the comma after "battleground site" becomes a colon, "adds one [Dark Side]" becomes "adds 1 [Dark Side] here", and "any" is added before "a card" ("any a card", as listed).
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_1002 extends Card1274_002 {
    public Card2274_1002() {
        super();
        setGameText("[Pilot] 2: any vehicle. While piloting a [Theed Palace] vehicle at a battleground site: adds 1 [Dark Side] here and may add 1 to your Force drains here. Once per game, may [upload] any a card with 'fire' in title.");
        setLegacyVersion(new LegacyVersion("1274_2", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
