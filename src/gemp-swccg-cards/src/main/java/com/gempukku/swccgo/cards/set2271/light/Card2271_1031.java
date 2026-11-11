package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.set1271.light.Card1271_031;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Title: •Biggs, Rogue Legend
 * PC-E1 version of 1271_31 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.1 release sheet original (2271_2031) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "one" is dropped ("any card").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1031 extends Card1271_031 {
    public Card2271_1031() {
        super();
        setGameText("[Pilot] 2, 3: Red 3. While piloting Red 3, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any card into hand from Force Pile; reshuffle.");
        setLegacyVersion(new LegacyVersion("1271_31", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
