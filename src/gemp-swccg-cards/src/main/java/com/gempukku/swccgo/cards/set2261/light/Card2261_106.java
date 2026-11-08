package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_250;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Starship
 * Title: Overseer
 * Original version of 1261_106: the Virtual Block 6.1 release sheet text, "If a Dark Jedi or Sith was just deployed"
 * (the PC card list, PC-E1 and now the current card, says "If opponent just deployed"). Same play: Dark Jedi and Sith
 * are Dark Side cards.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_106 extends Card601_250 {
    public Card2261_106() {
        setGameText("May add Harc as a pilot. During battle, adds one destiny to total power. If a Dark Jedi or Sith was just deployed to a related site, may ▼ a related location. Immune to attrition < 4 (< 6 at Bespin).");
        setLegacyVersion(new LegacyVersion("1261_106", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #110", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
