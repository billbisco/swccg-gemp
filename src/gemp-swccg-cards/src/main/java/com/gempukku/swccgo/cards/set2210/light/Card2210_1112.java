package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_112;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Red Leader
 *
 * PC-E1 version of 1210_112: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1112 extends Card1210_112 {
    public Card2210_1112() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While piloting at Dantooine system, when opponent deploys a character to a related site, they choose: opponent must lose 1 Force. OR that character may not battle or move this turn.");
        setLegacyVersion(new LegacyVersion("1210_112", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
