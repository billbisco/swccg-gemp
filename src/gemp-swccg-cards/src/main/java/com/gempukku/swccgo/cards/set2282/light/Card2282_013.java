package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_013;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Vilmarh 'Villie' Grahrk
 * Original version of 1282_13: the Virtual Block 8.2 release sheet (official text as of 2012-09-14; the surviving copy
 * was re-made 2012-10-18) printed this card as "•Devaronian Uncle" with the same game text and lore as the current
 * card; the PC website card list kept that title to 2013-01-11. Renamed Vilmarh 'Villie' Grahrk (PC card list by
 * 2013-03-13; Virtual Cards Master List 2013-04-27). Bill (2026-11-09): title changes are versions.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_013 extends Card1282_013 {
    public Card2282_013() {
        super();
        setLegacyVersion(new LegacyVersion("1282_13", "",
                "Virtual Block 8.2 release sheet (2012-09-14), printed as Devaronian Uncle",
                LocalDate.of(2012, 9, 14), LocalDate.of(2013, 3, 13)));
        setDisplayTitle("Devaronian Uncle");
    }
}
