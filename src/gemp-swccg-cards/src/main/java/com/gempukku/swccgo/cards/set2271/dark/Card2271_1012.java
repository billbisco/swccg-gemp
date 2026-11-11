package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Effect
 * Title: •Imperial Security Bureau
 * PC-E1 version of 1271_12 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.1 release sheet original (2271_2012) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "are" is dropped ("leaders deploy -2").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1012 extends Card1271_012 {
    public Card2271_1012() {
        super();
        setGameText("Deploy on table. Sunsdown is canceled. ISB agents who are leaders deploy -2 to sites with 'ISB' in title. Once per game, may [upload] a [Setup] Mission. Look Sir, Droids and your Interrupts with 'advisor' in title may be played as follows: 'USED: Use 1 Force to [upload] an ISB agent.' (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1271_12", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
