package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.cards.set1274.light.Card1274_017;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Device
 * Title: •Motivator
 * PC-E1 version of 1274_17 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2274_2017) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2274_3017), which prints the original words again.
 * Against the original: three changes: "Droid is" becomes "That droid is", the comma after the [Presence Droid] icon is dropped, and "one" is added before "R2-D2".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_1017 extends Card1274_017 {
    public Card2274_1017() {
        super();
        setGameText("Deploy on your non-[Reflections 2] astromech droid. That droid is power +2, gains [Presence Droid] and if present at a site, draws one battle destiny if unable to otherwise. During any move phase, may place this device in Lost Pile to [upload] one What're You Tryin' To Push On Us? or one R2-D2.");
        setLegacyVersion(new LegacyVersion("1274_17", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
