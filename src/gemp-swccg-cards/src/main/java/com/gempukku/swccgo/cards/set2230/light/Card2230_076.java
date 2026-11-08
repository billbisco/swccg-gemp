package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_076;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Master Luke (V)
 * Original version of 1230_76: the Virtual Block 3 release sheet text (Dec 2009), "place ... on owner's Force Pile" (the card list reads "in").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_076 extends Card1230_076 {
    public Card2230_076() {
        super();
        setGameText("Adds 2 to power of anything he pilots. During battle, may use 2 Force to place an opponent's alien (except Jabba or a Rep) present with Luke on owner's Force Pile (opponent may use or lose 3 Force instead). Immune to attrition < 4 (< 7 if with an alien leader).");
        setLegacyVersion(new LegacyVersion("1230_76", "",
                "Virtual Block 3 release sheet (2009-12-07), card #77", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
