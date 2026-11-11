package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Baragwin (V)
 * PC-E2 version of 1261_62: the PC card image (baragwin.gif), dated by its server upload (2013-02-25, a bulk upload; the
 * image may be older). It prints the release-sheet wording again; the Master List of 2013-04-27 (the current card) follows
 * it. Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2062 extends Card1261_062 {
    public Card2261_2062() {
        super();
        setGameText("Lost if a Starting Effect on table. Your non-alien characters are power and forfeit = 0. If present at a battleground site, your Force drains at battleground sites where you have two aliens are +1, and when you deploy an alien, retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1261_62", "PC-E2", "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
