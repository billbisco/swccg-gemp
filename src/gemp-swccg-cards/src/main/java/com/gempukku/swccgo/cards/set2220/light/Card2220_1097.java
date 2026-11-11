package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_097;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Title: •Princess Leia (V)
 * PC-E1 version of 1220_97 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 2 release sheet original (2220_97) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "a" is added before "matching blaster".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1097 extends Card1220_097 {
    public Card2220_1097() {
        super();
        setGameText("May deploy or move as a 'react' to same site as an Imperial. Adds one battle destiny if with Han or Vader. During your deploy phase, may [download] one Leia Of Alderaan, Reflection, or a matching blaster on Leia. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1220_97", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
