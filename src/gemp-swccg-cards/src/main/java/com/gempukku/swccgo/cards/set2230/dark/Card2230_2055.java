package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Capital
 * Title: Jabba's Space Cruiser (V)
 *
 * PC-E2 version of 1230_55: the PC card image (jabbasspacecruiser.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints a third wording (card-list order, release-sheet "'react.'"). The
 * Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version,
 * placed at its upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2055 extends Card601_012 {
    public Card2230_2055() {
        super();
        setGameText("Deploys and moves like a starfighter. Permanent pilot provides ability of 2. May add 1 alien pilot and 6 passengers. Your [Independent] starships may deploy here as a 'react.' If Jabba here, immune to attrition.");
        setLegacyVersion(new LegacyVersion("1230_55", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
