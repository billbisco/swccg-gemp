package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Rebel
 * Title: Kyle Katarn
 * PC-E1 version of 1281_28: the PC website card list text (2012-07-21 .. 2013-01-11), still titled Rebel Agent: "Your
 * total battle destiny +1 here, for each [Scoundrel] Effect on table" for "Your total battle destiny here is +1 for each
 * [Scoundrel] Effect on table", and "react" without quotes. Same play as the original. The Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card) printed the sheet wording under the new title Kyle Katarn.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1028 extends Card2281_028 {
    public Card2281_1028() {
        super();
        setGameText("May use any stolen weapon. Opponent may not react to or from same site. Your total battle destiny +1 here, for each [Scoundrel] Effect on table. Once per game, may make a regular move during your control phase. Immune to attrition < 4 (< 5 if armed).");
        setLegacyVersion(new LegacyVersion("1281_28", "PC-E1",
                "PC website card list (by 2012-07-21), printed as Rebel Agent",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 3, 13)));
    }
}
