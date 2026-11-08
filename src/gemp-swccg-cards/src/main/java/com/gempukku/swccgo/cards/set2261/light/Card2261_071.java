package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_052;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Luke Skywalker, Strong In The Force
 * Original version of 1261_71: the Virtual Block 6.1 release sheet text, "When leaving table, place all your cards on Luke" (the PC card list, PC-E1, says "When Luke leaves table ... on him").
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_071 extends Card601_052 {
    public Card2261_071() {
        setGameText("[Pilot] 3. While piloting Red 5 or Rogue 1 (or armed with Luke's Lightsaber), may add one destiny to total power or attrition, and I Have You Now is canceled. When leaving table, place all your cards on Luke in Used Pile. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1261_71", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #67", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
