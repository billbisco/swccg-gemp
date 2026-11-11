package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_091;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Uncharted Settlements
 *
 * PC-E1 version of 1240_91: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2240_1091 extends Card1240_091 {
    public Card2240_1091() {
        super();
        setGameText("Deploy on [Virtual Block 4] Hidden Base. Organized Attack, S-foils, Revolution, Your Destiny (unless you chose Rebel/Rebel Starship), and [Virtual Block 4] Ellors Madak are canceled. You may not deploy operatives or [Maintenance] cards. Force generation at Endor system may not be canceled. Unless Leave Them To Me on table, opponent's combat vehicles, Dark Jedi, [Maintenance] cards, and [Permanent Weapon] cards are deploy +2 to Rebel Cell sites. Opponent generates no Force at Hidden Landing Site. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_91", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
