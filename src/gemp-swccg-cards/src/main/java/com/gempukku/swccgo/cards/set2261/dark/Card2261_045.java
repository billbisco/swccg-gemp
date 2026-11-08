package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: One Bright Spot
 * Original version of 1261_45: the Virtual Block 6.1 release sheet text (Sep 2009), naming Black Leader (the Juno persona); same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_045 extends Card1261_045 {
    public Card2261_045() {
        setGameText("If Galen was just lost and Black Leader is on table (or vice versa), place the just-lost character on your Used Pile instead. OR If Black Leader is participating in a battle, you may lose Galen from hand to satisfy all battle damage and attrition against you.");
        setLegacyVersion(new LegacyVersion("1261_45", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #46", LocalDate.of(2009, 9, 4), LocalDate.of(2013, 4, 27)));
    }
}
