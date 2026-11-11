package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_174;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Scomp Link Access (V)
 * PC-E2 version of 1261_100 (Batch R8-U5, Bill 2026-11-11: every publication is a version; PC card
 * images are versions at their proven date): PC card image (on the PC server by 2013-02-25). Order so far: original sheet (2009-09-04) -> PC-E1 cl (2012-07-21) -> PC-E2 pc_srv (2013-02-25); the current card is the
 * Virtual Cards Master List (2013-04-27). Same play as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_3100 extends Card601_174 {
    public Card2261_3100() {
        super();
        setGameText("USED: a ▲ non-[Episode I] droid (except IL-19). OR Place a card from hand on Used Pile. STARTING: ▼ two always [Immune to Alter] Effects. If you have only one location (with < 3 [Light Side Force]) (except Massassi Throne Room) on table, may ▼ a [Cloud City] site. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1261_100", "PC-E2",
                "PC card image (on the PC server by 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
