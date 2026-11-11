package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_076;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Rebel
 * Title: Master Luke
 *
 * PC-E2 version of 1230_76: the PC card image (masterluke.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2076 extends Card1230_076 {
    public Card2230_2076() {
        super();
        setGameText("Adds 2 to power of anything he pilots. During battle, may use 2 Force to place an opponent's alien (except Jabba or a Rep) present with Luke on owner's Force Pile (opponent may use or lose 3 Force instead). Immune to attrition < 4 (< 7 if with an alien leader).");
        setLegacyVersion(new LegacyVersion("1230_76", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
