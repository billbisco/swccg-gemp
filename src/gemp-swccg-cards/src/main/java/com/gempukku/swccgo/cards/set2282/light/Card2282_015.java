package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_015;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Verrack (V)
 * Original version of 1282_15 (Virtual Block 8.2 release sheet): the sheet wording, before the PC website card list rephrase (2012-12-12, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_015 extends Card1282_015 {
    public Card2282_015() {
        super();
        setGameText("Adds 1 to power of anything he pilots. While aboard your starship, its total weapon destiny is +1. Whenever you hit a starship here, opponent loses 1 Force. During battle, may lose 1 Force to cancel [Coruscant] Watch Your Back!.");
        setLegacyVersion(new LegacyVersion("1282_15", "", "Virtual Block 8.2 release sheet (2012-09-14)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }
}
