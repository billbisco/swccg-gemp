package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Title: Anakin Solo
 * PC-E2 version of 1282_12: the Virtual Cards Master List (2013-04-27), which repeats the 2012 release-sheet words
 * ("Once per turn") and plays as the original (2282_12). Superseded by the PC card image (PC-E3, folder upload date
 * 2013-05-07), the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_2012 extends Card2282_012 {
    public Card2282_2012() {
        super();
        setLegacyVersion(new LegacyVersion("1282_12", "PC-E2",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
