package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_070;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Obi-Wan, Crazy Wizard
 * Original version of 1261_70: the Virtual Block 6.1 release sheet text, "may target this site like a Tatooine site" (the PC card list, PC-E1, says "even if not a Tatooine site").
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_070 extends Card1261_070 {
    public Card2261_070() {
        setGameText("Old Ben is a Used Interrupt and may target this site like a Tatooine site. Dark Jedi may not 'swing' lightsabers more than once per battle here. When leaving table, place all your cards on Obi-Wan in Used Pile. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1261_70", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #68", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
