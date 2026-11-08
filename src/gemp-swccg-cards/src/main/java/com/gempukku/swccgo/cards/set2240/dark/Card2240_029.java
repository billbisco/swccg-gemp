package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Krayt Dragon Bones
 * Original version of 1240_29: the Virtual Block 4 release sheet text (Dec 2009, slip 30E); the PC website card list
 * (by 2012-07-21) has the current wording ("because a creature has eaten" became "because a creatue has eaten"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_029 extends Card1240_029 {
    public Card2240_029() {
        super();
        setGameText("Deploy on table. Womp Rats are not [Selective Creature]. Once per turn, may [download] a creature (except a parasite) or a dewback. Your ferocious creatures are deploy -2 and ferocity +1. Whenever you retrieve Force because a creature has eaten a card, you need only use X-2 Force for Aim High (to a minimum of 1). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_29", "",
                "Virtual Block 4 release sheet (2009-12-07), card #30E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
