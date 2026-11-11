package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_074;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Lieutenant Blount
 * PC-E2 version of 1230_74. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2230_74) = the Virtual Block 3 release sheet (2009-12-07), card #75; PC-E1
 * (2230_1074) = the PC website card list (by 2012-07-21): two changes: “OR If you just targeted” becomes “OR if you
 * just targeted”, and “OR May target” becomes “OR may target” (small i and small m). PC-E2 (2230_2074) = the PC card
 * image (uploaded 2013-02-25; it may have been made earlier), the original words again; PC-E3 = the Master List
 * (2013-04-27), the original words again, the current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2074 extends Card1230_074 {
    public Card2230_2074() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Double Agent is a Used Interrupt. Once per game: may take Double Agent into hand from Reserve Deck; reshuffle. OR If you just targeted with Double Agent, may add 2 to your total destiny. OR May target opponent's bounty hunter with Double Agent instead of their spy.");
        setLegacyVersion(new LegacyVersion("1230_74", "PC-E2",
                "PC card image (server upload 2013-02-25; the original words again); superseded by the Master List (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
