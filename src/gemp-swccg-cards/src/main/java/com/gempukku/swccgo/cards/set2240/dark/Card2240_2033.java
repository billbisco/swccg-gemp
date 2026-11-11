package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_193;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Kuat Drive Yards (V)
 *
 * PC-E2 version of 1240_33: the PC card image (kuatdriveyards.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2033 extends Card601_193 {
    public Card2240_2033() {
        super();
        setGameText("Deploy on table. Unique (•) Imperial-class Star Destroyers draw one battle destiny if not able to otherwise and are immune to attrition < 4. During your deploy phase, may use 2 Force to [download] a battleground system. We're In Attack Position Now is canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_33", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
