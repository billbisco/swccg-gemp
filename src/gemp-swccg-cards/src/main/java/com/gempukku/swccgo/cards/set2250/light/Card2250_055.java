package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Yoda, Great Warrior
 *
 * Original version of 1250_55: the Virtual Block 5 release sheet (2009-12-07) text, card #56E. The PC card image
 * (PC-E1), in swccgvkit-legacy svn r1 by 2009-12-09, follows it. The Master List of 2013-04-27 (PC-E3, the current
 * card) prints this text again word for word. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_055 extends Card601_062 {
    public Card2250_055() {
        setGameText("Deploys only to battlegrounds. Power +3 while defending a battle. You may not deploy [Episode I] Jedi. While with a Wookiee, adds one battle destiny. Once per game, may lose 2 Force from Force Pile to cancel a non-[Immune to Sense] Interrupt. Immune to attrition.");
        setLegacyVersion(new LegacyVersion("1250_55", "",
                "Virtual Block 5 release sheet (2009-12-07), card #56E; superseded by the PC card image (in swccgvkit-legacy svn r1 by 2009-12-09)", LocalDate.of(2009, 12, 7), LocalDate.of(2009, 12, 9)));
    }
}
