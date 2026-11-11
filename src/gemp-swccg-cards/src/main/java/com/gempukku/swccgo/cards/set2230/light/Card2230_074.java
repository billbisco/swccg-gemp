package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_074;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Lieutenant Blount
 * Original version of 1230_74: the text of the Virtual Block 3 release sheet (2009-12-07), card #75; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2230_1074) changes it: two changes: “OR If you
 * just targeted” becomes “OR if you just targeted”, and “OR May target” becomes “OR may target” (small i and small
 * m). The Master List of 2013-04-27 prints the original words again; it is listed as its own version (PC-E2, the
 * current card; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2230_074 extends Card1230_074 {
    public Card2230_074() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Double Agent is a Used Interrupt. Once per game: may take Double Agent into hand from Reserve Deck; reshuffle. OR If you just targeted with Double Agent, may add 2 to your total destiny. OR May target opponent's bounty hunter with Double Agent instead of their spy.");
        setLegacyVersion(new LegacyVersion("1230_74", "",
                "Virtual Block 3 release sheet (2009-12-07), card #75; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
