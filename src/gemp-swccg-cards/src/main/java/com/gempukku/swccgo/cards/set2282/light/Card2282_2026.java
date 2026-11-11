package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Title: Under Jabba's Protection
 *
 * PC-E2 version of 1282_26: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_2026 extends Card1282_026 {
    public Card2282_2026() {
        super();
        setGameText("When you play this Interrupt, may place a Rep on your Objective in Used Pile. Lose X cards stacked on I Know That Laugh to reduce a weapon or battle destiny total by X. OR Use 1 Force to take a card stacked on I Know That Laugh into hand (or stack top card of Lost Pile on it).");
        setLegacyVersion(new LegacyVersion("1282_26", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
