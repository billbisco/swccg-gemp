package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used
 * Title: Under Jabba's Protection
 * Original version of 1282_26 (Virtual Block 8.2 release sheet): the sheet wording, before the PC website card list rephrase (2012-12-12, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_026 extends Card1282_026 {
    public Card2282_026() {
        super();
        setGameText("When you play this Interrupt, may place a Rep on your Objective in Used Pile. Lose X cards stacked on I Know That Laugh to reduce a weapon or battle destiny total by X. OR Use 1 Force to take a card stacked on I Know That Laugh into hand (or stack top card of Lost Pile on it).");
        setLegacyVersion(new LegacyVersion("1282_26", "", "Virtual Block 8.2 release sheet (2012-09-14)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }
}
