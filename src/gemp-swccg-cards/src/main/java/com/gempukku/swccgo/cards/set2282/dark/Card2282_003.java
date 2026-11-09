package com.gempukku.swccgo.cards.set2282.dark;

import com.gempukku.swccgo.cards.set1282.dark.Card1282_003;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Effect
 * Title: You May Start Your Landing (V)
 * Original version of 1282_3: the Virtual Block 8.2 release sheet text (2012-09-14; the PC card image and the Master List of 2013-04-27
 * print it). The PC website card list (by 2012-12-12) prints "AT-At" and "Garrrison" (PC-E1, typos as published). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_003 extends Card1282_003 {
    public Card2282_003() {
        super();
        setGameText("If 5th Marker on table, deploy on table. During your control phase, opponent loses 1 Force for each marker site you control with an AT-AT (limit 1 unless [Virtual Block 2] Imperial Control on table). Once per game, may take Walker Garrison and/or 3rd Marker into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1282_3", "",
                "Virtual Block 8.2 release sheet (2012-09-14); superseded by the PC card list text (by 2012-12-12)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }
}
