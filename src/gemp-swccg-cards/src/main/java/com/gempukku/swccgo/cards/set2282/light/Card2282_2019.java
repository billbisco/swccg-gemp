package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Title: Rennek (V)
 * PC-E2 version of 1282_19: the Virtual Cards Master List (2013-04-27), which repeats the release-sheet words. The PC card
 * image (folder upload date 2013-05-07, PC-E3) prints the same words and is the current card, so this version plays as
 * the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_2019 extends Card1282_019 {
    public Card2282_2019() {
        super();
        setGameText("May be stacked on I Know That Laugh from hand, using 2 Force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reset a participating character's forfeit to 0.");
        setLegacyVersion(new LegacyVersion("1282_19", "PC-E2",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
