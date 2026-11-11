package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_070;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Obi-Wan, Crazy Wizard
 *
 * PC-E2 version of 1261_70: the PC card image (obiwancrazywizard.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2070 extends Card1261_070 {
    public Card2261_2070() {
        super();
        setGameText("Old Ben is a Used Interrupt and may target this site like a Tatooine site. Dark Jedi may not 'swing' lightsabers more than once per battle here. When leaving table, place all your cards on Obi-Wan in Used Pile. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1261_70", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
