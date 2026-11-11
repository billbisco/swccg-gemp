package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_035;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Republic
 * Title: Clone Pilot
 *
 * PC-E1 version of 1250_35: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2250_1035 extends Card1250_035 {
    public Card2250_1035() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While piloting a [Republic] starship at a battleground, forfeit +1 and your Force drains are +1 here. Anything he pilots is maneuver +1 and immune to attrition < 3 (< 5 if all your pilot characters here are clones and/or Jedi).");
        setLegacyVersion(new LegacyVersion("1250_35", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
