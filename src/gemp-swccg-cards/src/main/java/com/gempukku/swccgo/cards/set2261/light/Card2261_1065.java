package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_065;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: BoShek, Brash Smuggler
 * PC-E1 version of 1261_65: the PC website card list text, "When piloting a freighter" (the current Master List text
 * says "While"). Same play as the current card. Image: the current card image.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1065 extends Card1261_065 {
    public Card2261_1065() {
        setGameText("Adds 3 to power of anything he pilots. When piloting a freighter at a battleground system, adds 1 to attrition against opponent here and their starships here may not 'cloak.' Opponent loses no more than 3 Force to your Force drains at same system.");
        setLegacyVersion(new LegacyVersion("1261_65", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
