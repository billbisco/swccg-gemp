package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_094;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Effect
 * Title: Revenge Of The Sith
 * Original version of 1263_10: the Virtual Block 6.3 release sheet text, "(maximum of 8)" (the PC card list, PC-E1, says "to a maximum of 8").
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_010 extends Card601_094 {
    public Card2263_010() {
        setGameText("Deploy on Vader. [Coruscant] holograms are lost. Opponent may not add destiny draws to total power or attrition here. While with a Skywalker or Jedi, Vader's defense value is +2 (maximum of 8) and his immunity to attrition is +2. When Vader leaves table, place Effect in Used Pile. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1263_10", "",
                "Virtual Block 6.3 release sheet (2010-03-15), card #3-10E", LocalDate.of(2010, 3, 15), LocalDate.of(2012, 7, 21)));
    }
}
