package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_014;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Expand The Empire (V)
 *
 * PC-E1 version of 1250_14: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2250_1014 extends Card1250_014 {
    public Card2250_1014() {
        super();
        setGameText("Deploy on Senate. May raise your converted Senate to the top. Jedi here are politics +4. While your total politics here is > 5, at battlegrounds where you have an Imperial, your Force drains and battle destiny draws are +1. Your aliens (except Mara), senators, Maul, and [Independent Starship] starships are lost. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_14", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
