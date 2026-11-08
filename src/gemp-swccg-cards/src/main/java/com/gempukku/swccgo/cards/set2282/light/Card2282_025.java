package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_025;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used
 * Title: You Will Take Me To Jabba Now (V)
 * Original version of 1282_25 (Virtual Block 8.2 release sheet), before the PC website card list rephrase (2012-12-12, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_025 extends Card1282_025 {
    public Card2282_025() {
        super();
        setGameText("When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile. Deploy Seeking An Audience or an alien to Audience Chamber (for -1 Force) from Reserve Deck; reshuffle. OR Reveal up to 3 aliens stacked on I Know That Laugh or from hand (X = number of aliens revealed); activate X Force.");
        setLegacyVersion(new LegacyVersion("1282_25", "", "Virtual Block 8.2 release sheet (2012-09-14)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }
}
