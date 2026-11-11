package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_041;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Dead Ewok
 *
 * PC-E2 version of 1230_41: the PC card image (deadewok.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2041 extends Card1230_041 {
    public Card2230_2041() {
        super();
        setGameText("Cancel Ellorrs Madak. (Immune to Sense.) OR Use 2 Force to cancel the game text of an opponent's alien for remainder of turn. OR Place an Ewok Catapult in opponent's Used Pile. OR Make an Ewok power and forfeit = 0 for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1230_41", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
