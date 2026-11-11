package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Title: Anakin Solo
 * PC-E1 version of 1282_12: the PC website card list text (by 2012-12-12): "Once during battle", "<3 (<5". Plays as
 * the current card (the PC card image, PC-E3). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1012 extends Card1282_012 {
    public Card2282_1012() {
        super();
        setGameText("Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once during battle, where present, may steal an opponent's just-lost lightsaber. Immune to attrition <3 (<5 while armed with a 'stolen' lightsaber).");
        setLegacyVersion(new LegacyVersion("1282_12", "PC-E1",
                "PC website card list (by 2012-12-12)", LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27)));
    }
}
