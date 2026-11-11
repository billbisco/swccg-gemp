package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_151;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Title: Gardulla The Hutt (V)
 *
 * PC-E2 version of 1274_1: the PC card image (gardullathehutt.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_2001 extends Card601_151 {
    public Card2274_2001() {
        super();
        setGameText("During battle, if [Block 4] No Bargain on table, may lose 2 Force to cancel a non-[Immune To Sense] Interrupt. Once per game, during your move phase, may relocate any of your characters here to a site you occupy as a regular move (or vice versa). Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1274_1", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
