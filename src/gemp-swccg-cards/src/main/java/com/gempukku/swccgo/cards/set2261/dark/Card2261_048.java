package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_098;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Weapon Levitation & The Empire's Back
 * Original version of 1261_48: the Virtual Block 6.1 release sheet text, "Cancel Disarmed or Wookiee Strangle". The PC
 * card list (PC-E1, the current card 601_98, 2012-07-21 to 2013-01-11) spells it "Wookie". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_048 extends Card601_098 {
    public Card2261_048() {
        setGameText("USED: Cancel Disarmed or Wookiee Strangle. OR Once per battle involving Vader, reveal a device or character weapon from Used Pile and add its printed destiny number to your total power; reshuffle. LOST: Use 1 Force to ▼ (or retrieve) Vader. OR If opponent just initiated a battle against Vader defending alone at a site, his forfeit cannot be reduced.");
        setLegacyVersion(new LegacyVersion("1261_48", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #51", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
