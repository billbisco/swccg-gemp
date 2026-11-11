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
 * PC-E2 version of 1220_62: the PC card image (levitationattack.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the card-list wording (icons drawn). The Master List of
 * 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its
 * upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2062 extends Card1220_062 {
    public Card2220_2062() {
        super();
        setGameText("Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During a battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present).");
        setLegacyVersion(new LegacyVersion("1220_62", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
