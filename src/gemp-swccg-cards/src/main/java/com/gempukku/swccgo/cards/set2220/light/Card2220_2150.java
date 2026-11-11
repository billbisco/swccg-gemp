package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_150;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: NOOOOOOOOOOOO!
 *
 * PC-E2 version of 1220_150: the PC card image (noooooooooooo.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2150 extends Card1220_150 {
    public Card2220_2150() {
        super();
        setGameText("Cancel Broken Concentration, The Circle Is Now Complete, or a duel initiated during battle. (Immune to Young Fool.) OR During any draw phase, place a card from hand under your Used Pile to take any one card into hand from Force Pile; reshuffle.");
        setLegacyVersion(new LegacyVersion("1220_150", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
