package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Krayt Dragon Bones (V)
 *
 * PC-E2 version of 1240_29: the PC card image (kraytdragonbones.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2029 extends Card1240_029 {
    public Card2240_2029() {
        super();
        setGameText("Deploy on table. Womp Rats are not [Selective Creature]. Once per turn, may [download] a creature (except a parasite) or a dewback. Your ferocious creatures are deploy -2 and ferocity +1. Whenever you retrieve Force because a creature has eaten a card, you need only use X-2 Force for Aim High (to a minimum of 1). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_29", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
