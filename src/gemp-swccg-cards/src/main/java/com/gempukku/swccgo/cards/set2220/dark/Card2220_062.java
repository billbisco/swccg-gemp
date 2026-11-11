package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Title: Levitation Attack
 *
 * PC-E2 version of 1220_62: the Virtual Block 2 release sheet (2009-12-07) text, card #62E ("During battle"). The
 * PC card image (PC-E1, the 2009-09-09 errata) is older. The Master List of 2013-04-27 (PC-E4, the current card)
 * prints this text again word for word. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_062 extends Card1220_062 {
    public Card2220_062() {
        super();
        setGameText("Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present).");
        setLegacyVersion(new LegacyVersion("1220_62", "PC-E2",
                "Virtual Block 2 release sheet (2009-12-07), card #62E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
