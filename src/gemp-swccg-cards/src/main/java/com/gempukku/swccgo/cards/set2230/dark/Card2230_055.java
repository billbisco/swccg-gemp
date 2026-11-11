package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Title: Jabba's Space Cruiser (V)
 *
 * Original version of 1230_55: the Virtual Block 3 release sheet (2009-12-07) text, card #55E. The PC card image
 * (PC-E1), in swccgvkit-legacy svn r1 by 2009-12-09, follows it. The Master List of 2013-04-27 (PC-E3, the current
 * card) prints this text again word for word. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_055 extends Card601_012 {
    public Card2230_055() {
        super();
        setGameText("Deploys and moves like a starfighter. May add 1 alien pilot and 6 passengers. Permanent pilot provides ability of 2. Your [Independent] starships may deploy here as a 'react.' If Jabba here, immune to attrition.");
        setLegacyVersion(new LegacyVersion("1230_55", "",
                "Virtual Block 3 release sheet (2009-12-07), card #55E; superseded by the PC card image (in swccgvkit-legacy svn r1 by 2009-12-09)", LocalDate.of(2009, 12, 7), LocalDate.of(2009, 12, 9)));
    }
}
