package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_211;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Starship
 * Subtype: Starfighter
 * Title: Stinger (V)
 *
 * PC-E2 version of 1240_63: the PC card image (stinger.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2063 extends Card601_211 {
    public Card2240_2063() {
        super();
        setGameText("May add 1 pilot. Guri deploys -3 aboard. While Guri piloting, during any control phase may draw top card of Reserve Deck (if Chewie or Leia on table, Force Pile instead) and immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1240_63", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
