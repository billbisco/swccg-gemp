package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Great Warrior
 *
 * PC-E2 version of 1250_55: the PC website card list text (by 2012-07-21 to 2013-01-11; generated from the PC's
 * Holotable card data). The Master List of 2013-04-27 (PC-E3, the current card) prints the release-sheet wording
 * again. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1055 extends Card601_062 {
    public Card2250_1055() {
        super();
        setGameText("Deploys only to battlegrounds. Power +3 when defending a battle. You may not deploy [Episode I] Jedi. While with a Wookiee, adds one battle destiny. Once per game, may lose 2 Force from Force Pile to cancel a non-[Immune to Sense] Interrupt. Immune to attrition.");
        setLegacyVersion(new LegacyVersion("1250_55", "PC-E2",
                "PC website card list (by 2012-07-21); superseded by the Master List text (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
