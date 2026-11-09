package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Title: Levitation Attack
 * Original version of 1220_62: the Virtual Block 2 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "During a battle" for "During battle"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_062 extends Card1220_062 {
    public Card2220_062() {
        super();
        setGameText("Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present).");
        setLegacyVersion(new LegacyVersion("1220_62", "",
                "Virtual Block 2 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
