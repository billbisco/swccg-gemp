package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_083;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Weapon
 * Subtype: Vehicle
 * Title: AT-AT Cannon
 *
 * PC-E1 version of 1220_83: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1083 extends Card1220_083 {
    public Card2220_1083() {
        super();
        setGameText("Deploy on your non-[Maintenance] AT-AT. May target a character or vehicle at same or adjacent site for free. Draw destiny. Target hit if destiny +1 (+2 if a vehicle) > defense value. If lost from table, place this weapon in Used Pile.");
        setLegacyVersion(new LegacyVersion("1220_83", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
