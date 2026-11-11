package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Coruscant Guard
 * PC-E1 version of 1250_4: the PC website card list text (by 2012-07-21; Virtual Block 5 list and Virtual master list
 * to 2013-01-11). Against the original (2250_4): two changes: “cumulatively politics +2” is written “cumulatively
 * Politics +2”, with a capital P, and “Expand The Empire” is written “Expand the Empire”, with a small t. The Master
 * List of 2013-04-27 (PC-E2, the current card) prints the original words again (swccg-guild/work#236; Bill
 * 2026-11-11: every publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1004 extends Card1250_004 {
    public Card2250_1004() {
        super();
        setGameText("Heroic. Deploys -1 to Senate. While at Senate, Sidious is cumulatively Politics +2, and, during your control phase, may place this character in Used Pile to take [Virtual Block 5] Expand the Empire into hand from Reserve Deck; reshuffle, or take any one card into hand from Force Pile; reshuffle.");
        setLegacyVersion(new LegacyVersion("1250_4", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
