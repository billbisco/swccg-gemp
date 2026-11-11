package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_075;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Rebel Scout
 *
 * PC-E1 version of 1230_75: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_1075 extends Card1230_075 {
    public Card2230_1075() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Deploys -2 to a forest. His weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. During opponent's draw phase, if alone on Endor, may retrieve 1 Force. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1230_75", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
