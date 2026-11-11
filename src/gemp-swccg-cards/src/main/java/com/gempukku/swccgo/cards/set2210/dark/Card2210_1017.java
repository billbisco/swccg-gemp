package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_152;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Prophetess (V)
 *
 * PC-E1 version of 1210_17: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1017 extends Card601_152 {
    public Card2210_1017() {
        super();
        setGameText("Deploy -1 and forfeit +3 at a site. Once per turn, may peek at top card of opponent's Reserve Deck or Used Pile; may then shuffle that Reserve Deck or Used Pile. Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1210_17", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
