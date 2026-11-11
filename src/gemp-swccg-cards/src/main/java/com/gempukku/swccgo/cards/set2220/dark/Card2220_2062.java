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
 * PC-E1 version of 1220_62: the PC card image (levitationattack.gif, slip 65E), the same image as in
 * swccgvkit-legacy svn r1 (2009-12-09). The PC card list stamps the card "Errata Text - September 9th, 2009", so
 * this wording is that errata of 2009-09-09 (H1 pattern; the September 2009 first printing is lost). Prints
 * "During a battle". Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2062 extends Card1220_062 {
    public Card2220_2062() {
        super();
        setGameText("Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During a battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present).");
        setLegacyVersion(new LegacyVersion("1220_62", "PC-E1",
                "PC card image (slip 65E; in swccgvkit-legacy svn r1, 2009-12-09), the 2009-09-09 errata", LocalDate.of(2009, 9, 9), LocalDate.of(2009, 12, 7)));
    }
}
