package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Levitation Attack
 *
 * PC-E3 version of 1220_62: the PC website card list text (by 2012-07-21 to 2013-01-11; generated from the PC's
 * Holotable card data), the PC-E1 wording again ("During a battle"). The Master List of 2013-04-27 (PC-E4, the
 * current card) prints the release-sheet wording again. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1062 extends Card1220_062 {
    public Card2220_1062() {
        super();
        setGameText("Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During a battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present).");
        setLegacyVersion(new LegacyVersion("1220_62", "PC-E3",
                "PC website card list (by 2012-07-21); superseded by the Master List text (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
