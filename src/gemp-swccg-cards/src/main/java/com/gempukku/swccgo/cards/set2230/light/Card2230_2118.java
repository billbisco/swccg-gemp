package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_118;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Weapon
 * Subtype: Starship
 * Title: Enhanced Proton Torpedoes
 *
 * PC-E2 version of 1230_118: the PC card image (enhancedprotontorpedoes.gif), dated by its server upload
 * (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of
 * 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its
 * upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2118 extends Card1230_118 {
    public Card2230_2118() {
        super();
        setGameText("Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny (If on a Heroic starfighter, may first use 1 Force to add 3 to total). Target hit if total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1230_118", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
