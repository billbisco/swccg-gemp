package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_074;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Lieutenant Blount
 * PC-E1 version of 1230_74: the PC website card list text (by 2012-07-21; Virtual Block 3 list and Virtual master
 * list to 2013-01-11). Against the original (2230_74): two changes: “OR If you just targeted” becomes “OR if you just
 * targeted”, and “OR May target” becomes “OR may target” (small i and small m). The Master List of 2013-04-27 (PC-E2,
 * the current card) prints the original words again (swccg-guild/work#236; Bill 2026-11-11: every publication is a
 * version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1074 extends Card1230_074 {
    public Card2230_1074() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Double Agent is a Used Interrupt. Once per game: may take Double Agent into hand from Reserve Deck; reshuffle. OR if you just targeted with Double Agent, may add 2 to your total destiny. OR may target opponent's bounty hunter with Double Agent instead of their spy.");
        setLegacyVersion(new LegacyVersion("1230_74", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
