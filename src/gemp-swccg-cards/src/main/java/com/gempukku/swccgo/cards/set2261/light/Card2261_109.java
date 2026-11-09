package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_109;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: 
 * Title: Dash In Rogue 10
 * Original version of 1261_109: the Virtual Block 6.1 release sheet (2009-09-04) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "about to draw battle destiny" for "about to draw a battle destiny"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_109 extends Card1261_109 {
    public Card2261_109() {
        super();
        setGameText("May add 1 pilot. Permanent pilot is •Dash, who provides ability of 3. During battle, if with another T-47 and about to draw a battle destiny, may draw two and choose one. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1261_109", "",
                "Virtual Block 6.1 release sheet (2009-09-04)", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
