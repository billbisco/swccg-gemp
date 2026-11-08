package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Device
 * Title: Jet Pack (V)
 * Original version of 1281_8 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_008 extends Card1281_008 {
    public Card2281_008() {
        super();
        setGameText("Use 2 Force to deploy on your alien warrior (free on Jodo Kast or any Fett). Character 'flies' (landspeed = 3 and maneuver = 6), may fire weapons for free, and may move as a 'react' for +1 Force.");
        setLegacyVersion(new LegacyVersion("1281_8", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
