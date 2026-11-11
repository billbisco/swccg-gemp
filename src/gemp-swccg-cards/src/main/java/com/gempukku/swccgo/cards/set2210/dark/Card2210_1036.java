package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Forced Servitude
 *
 * PC-E1 version of 1210_36: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1036 extends Card1210_036 {
    public Card2210_1036() {
        super();
        setGameText("Deploy on table. Your troopers are Heroic. Opponent's total weapon destiny targeting your Heroic cards is +2. When opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_36", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
