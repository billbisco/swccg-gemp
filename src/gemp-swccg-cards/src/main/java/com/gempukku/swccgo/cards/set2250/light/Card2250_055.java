package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Yoda, Great Warrior
 * Original version of 1250_55: the Virtual Block 5 release sheet text ("Power +3 while defending a battle"; the
 * PC card list, PC-E1, says "when"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_055 extends Card601_062 {
    public Card2250_055() {
        setGameText("Deploys only to battlegrounds. Power +3 while defending a battle. You may not deploy [Episode I] Jedi. While with a Wookiee, adds one battle destiny. Once per game, may lose 2 Force from Force Pile to cancel a non-[Immune to Sense] Interrupt. Immune to attrition.");
        setLegacyVersion(new LegacyVersion("1250_55", "",
                "Virtual Block 5 release sheet (2009-12-07), card #56E", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
