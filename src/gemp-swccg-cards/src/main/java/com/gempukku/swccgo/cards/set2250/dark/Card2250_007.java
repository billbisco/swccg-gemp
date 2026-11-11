package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Lord Sidious (Virtual Block 5)
 * Original version of 1250_7: the text of the Virtual Block 5 release sheet (2009-12-07), card #08; the PC card image
 * prints it. The PC website card list (by 2012-07-21, PC-E1, 2250_1007) changes it: “you have no senators here” is
 * written “you have no Senators here”, with a capital S. The Master List of 2013-04-27 prints the original words
 * again; it is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_007 extends Card1250_007 {
    public Card2250_007() {
        super();
        setGameText("Whenever a Jedi is forfeited and you have exactly two Dark Jedi on table, retrieve 2 Force. While at your Senate and you have no senators here, Sidious is politics +4 and his game text may not be canceled. Immune to [Virtual Block 1] Leia and attrition.");
        setLegacyVersion(new LegacyVersion("1250_7", "",
                "Virtual Block 5 release sheet (2009-12-07), card #08; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
