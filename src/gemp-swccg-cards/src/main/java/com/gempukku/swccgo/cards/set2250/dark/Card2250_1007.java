package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Lord Sidious (Virtual Block 5)
 * PC-E1 version of 1250_7: the PC website card list text (by 2012-07-21; Virtual Block 5 list and Virtual master list
 * to 2013-01-11). Against the original (2250_7): “you have no senators here” is written “you have no Senators here”,
 * with a capital S. The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again
 * (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2250_1007 extends Card1250_007 {
    public Card2250_1007() {
        super();
        setGameText("Whenever a Jedi is forfeited and you have exactly two Dark Jedi on table, retrieve 2 Force. While at your Senate and you have no Senators here, Sidious is politics +4 and his game text may not be canceled. Immune to [Virtual Block 1] Leia and attrition.");
        setLegacyVersion(new LegacyVersion("1250_7", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
