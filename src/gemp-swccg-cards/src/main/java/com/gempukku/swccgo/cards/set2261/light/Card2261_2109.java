package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_109;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Dash In Rogue 10
 *
 * PC-E2 version of 1261_109: the PC card image (dashinrogue10.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2109 extends Card1261_109 {
    public Card2261_2109() {
        super();
        setGameText("May add 1 pilot. Permanent pilot is •Dash, who provides ability of 3. During battle, if with another T-47 and about to draw a battle destiny, may draw two and choose one. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1261_109", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
