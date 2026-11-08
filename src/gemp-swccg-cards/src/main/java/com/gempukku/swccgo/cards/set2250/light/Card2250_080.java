package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_175;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Starship
 * Title: Bravo Fighter (V)
 * Original version of 1250_80: the Virtual Block 5 release sheet text (the PC card list, PC-E1 and now the current
 * card, moves the permanent pilot sentence and says "one astromech"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_080 extends Card601_175 {
    public Card2250_080() {
        setGameText("Permanent pilot provides ability of 4. May add 1 astromech. During battle, may target a starship with no character piloting. Draw destiny. If destiny > defense value, target hit. Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1250_80", "",
                "Virtual Block 5 release sheet (2009-12-07), card #81", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
