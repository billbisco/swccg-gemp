package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Krayt Dragon Bones (V)
 *
 * PC-E1 version of 1240_29: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2240_1029 extends Card1240_029 {
    public Card2240_1029() {
        super();
        setGameText("Deploy on table. Womp Rats are not [Selective Creature]. Once per turn, may [download] a creature (except a parasite) or a dewback. Your ferocious creatures are deploy -2 and ferocity +1. Whenever you retrieve Force because a creatue has eaten a card, you need only use X-2 Force for Aim High (to a minimum of 1). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_29", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
