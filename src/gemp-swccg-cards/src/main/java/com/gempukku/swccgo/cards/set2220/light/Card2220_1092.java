package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Jeroen Webb
 *
 * PC-E1 version of 1220_92: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1092 extends Card1220_092 {
    public Card2220_1092() {
        super();
        setGameText("If deploying to opponent's mobile site, deploys as an Undercover spy (deploy -2). Nevar Yalnal may not target spies at Webb's site. Opponent's total influence here is -1 for each of your spies at sites.");
        setLegacyVersion(new LegacyVersion("1220_92", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
