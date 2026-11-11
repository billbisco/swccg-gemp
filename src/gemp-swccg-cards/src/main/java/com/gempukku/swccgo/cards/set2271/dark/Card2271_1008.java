package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Effect
 * Title: •A Sith's Weapon
 * PC-E1 version of 1271_8 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.1 release sheet original (2271_2008) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "this Effect" is dropped ("May place in Used Pile").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1008 extends Card1271_008 {
    public Card2271_1008() {
        super();
        setGameText("Deploy on table. Once per turn, if your character of ability > 4 just failed to 'hit' an [Episode I] Jedi (or [Virtual Block 6] Luke) with a non-[Permanent Weapon] lightsaber, may lose 1 Force to 'swing' at target again. May place in Used Pile to cancel Obi-Wan's Journal.");
        setLegacyVersion(new LegacyVersion("1271_8", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
