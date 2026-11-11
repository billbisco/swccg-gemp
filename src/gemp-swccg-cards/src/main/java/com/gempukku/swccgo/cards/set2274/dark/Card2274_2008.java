package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set1274.dark.Card1274_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Title: Shadows Of The Empire
 *
 * PC-E2 version of 1274_8: the PC card image (shadowsoftheempire.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_2008 extends Card1274_008 {
    public Card2274_2008() {
        super();
        setGameText("Deploy on Agents Of Black Sun. Mistryl are lost. Once per game, may [upload] Secret Plans. May use 3 Force to [download] Imperial Square, and once per turn, if Emperor Palpatine there, may draw a card from Force Pile (and if you occupy three battlegrounds, opponent loses 1 Force). [Immune to Alter.]");
        setLegacyVersion(new LegacyVersion("1274_8", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
