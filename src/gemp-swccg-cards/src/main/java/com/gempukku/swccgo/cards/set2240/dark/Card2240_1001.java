package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_180;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Arica (V)
 *
 * PC-E1 version of 1240_1: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated from
 * the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the release-sheet
 * wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2240_1001 extends Card601_180 {
    public Card2240_1001() {
        super();
        setGameText("Power +1 on Tatooine. Once per game, during your move phase, may relocate Arica to Luke's location. During your control phase, if present at a battleground (and Luke is not), may retrieve 1 Force. Immune to Undercover and attrition < 4.");
        setLegacyVersion(new LegacyVersion("1240_1", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
