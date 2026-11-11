package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_052;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Luke Skywalker, Strong In The Force
 *
 * PC-E2 version of 1261_71: the PC card image (lukeskywalkerstrongintheforce.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2071 extends Card601_052 {
    public Card2261_2071() {
        super();
        setGameText("[Pilot] 3. While piloting Red 5 or Rogue 1 (or armed with Luke's Lightsaber), may add one destiny to total power or attrition, and I Have You Now is canceled. When leaving table, place all your cards on Luke in Used Pile. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1261_71", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
