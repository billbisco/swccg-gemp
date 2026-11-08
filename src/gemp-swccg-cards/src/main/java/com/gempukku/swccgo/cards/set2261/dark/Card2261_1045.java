package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: One Bright Spot
 * PC-E1 version of 1261_45: the Virtual Cards Master List text (2013-04-27), Juno Eclipse; same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1045 extends Card1261_045 {
    public Card2261_1045() {
        setGameText("If Galen was just lost and Juno Eclipse is on table (or vice versa), place the just-lost character on your Used Pile instead. OR If Juno Eclipse is participating in a battle, may lose Galen from hand to satisfy all battle damage and attrition against you.");
        setLegacyVersion(new LegacyVersion("1261_45", "PC-E1",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), null));
    }
}
