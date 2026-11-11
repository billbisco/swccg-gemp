package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Coruscant Guard
 * Original version of 1250_4. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at
 * its server upload date): original (2250_4) = the Virtual Block 5 release sheet (2009-12-07), card #04; PC-E1
 * (2250_1004) = the PC website card list (by 2012-07-21): two changes: “cumulatively politics +2” is written
 * “cumulatively Politics +2”, with a capital P, and “Expand The Empire” is written “Expand the Empire”, with a small
 * t. PC-E2 (2250_2004) = the PC card image (uploaded 2013-02-25; it may have been made earlier), the original words
 * again; PC-E3 = the Master List (2013-04-27), the original words again, the current card. Same play. Anything Goes
 * only (docs/legacy-versions.md).
 */
public class Card2250_004 extends Card1250_004 {
    public Card2250_004() {
        super();
        setGameText("Heroic. Deploys -1 to Senate. While at Senate, Sidious is cumulatively politics +2, and, during your control phase, may place this character in Used Pile to take [Virtual Block 5] Expand The Empire into hand from Reserve Deck; reshuffle, or take any one card into hand from Force Pile; reshuffle.");
        setLegacyVersion(new LegacyVersion("1250_4", "",
                "Virtual Block 5 release sheet (2009-12-07), card #04; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
