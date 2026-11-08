package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_028;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Tarkin's Bounty (V)
 * Original version of 1261_40: the Virtual Block 6.1 release sheet text ("may ▲ [Virtual] Astromech Shortage"; the PC
 * card list, PC-E1, says "may take one [Virtual] Astromech Shortage into hand from Reserve Deck; reshuffle"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_040 extends Card601_028 {
    public Card2261_040() {
        setGameText("Deploy on table. Each Amidala stacked on a Political Effect is a senator. While you have < 13 cards in hand, opponent may not peek at or remove them (except with Grimtaash). Once per turn, may ▲ [Virtual] Astromech Shortage or lose 2 Force to cancel a Political Effect. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_40", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #40", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
