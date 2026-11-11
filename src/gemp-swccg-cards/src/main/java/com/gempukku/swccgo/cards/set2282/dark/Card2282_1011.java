package com.gempukku.swccgo.cards.set2282.dark;

import com.gempukku.swccgo.cards.set1282.dark.Card1282_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Weapon
 * Title: Assassin's Blaster Rifle
 * PC-E1 version of 1282_11: the PC website card list text (by 2012-12-12): "Deploy on an assassin", "may not be
 * captured". The same words as the current card (the PC card image, PC-E3), so it plays as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1011 extends Card1282_011 {
    public Card2282_1011() {
        super();
        setLegacyVersion(new LegacyVersion("1282_11", "PC-E1",
                "PC website card list (by 2012-12-12)", LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27)));
    }
}
