package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_030;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Wes Janson, Rogue Veteran
 *
 * PC-E2 version of 1273_30: the PC card image (wesjansonrogueveteran.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2030 extends Card1273_030 {
    public Card2273_2030() {
        super();
        setGameText("[Pilot] 2. Once per turn, may [download] (or deploy from hand) Disruptor Pistol for free on Wes. May fire Disruptor Pistol for 1 Force. Power and forfeit +1 while with Hobbie or Wedge. If about to leave table, place cards on Wes in owner's Used Pile.");
        setLegacyVersion(new LegacyVersion("1273_30", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
