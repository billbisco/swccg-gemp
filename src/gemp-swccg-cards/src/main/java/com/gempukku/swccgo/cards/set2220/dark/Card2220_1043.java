package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_043;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Precision Targeting
 *
 * PC-E1 version of 1220_43: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1043 extends Card1220_043 {
    public Card2220_1043() {
        super();
        setGameText("Deploy on table. Your capital starship weapon destiny draws are +1. During your deploy phase, may use 1 Force to [download] a starship weapon (for free) on you capital starship. If a starship was just hit, may place this Effect in Lost Pile to make that starship forfeit = 0.");
        setLegacyVersion(new LegacyVersion("1220_43", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
