package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Tusken Breath Mask (V)
 *
 * PC-E1 version of 1240_92: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2240_1092 extends Card1240_092 {
    public Card2240_1092() {
        super();
        setGameText("Deploy on an exterior Tatooine battleground site. X = the face up destiny number of your Objective. Once per turn, if all your ability on table is provided by aliens, may add X to your total ability during battle at a site. This Effect canceled if opponent controls this site.");
        setLegacyVersion(new LegacyVersion("1240_92", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
