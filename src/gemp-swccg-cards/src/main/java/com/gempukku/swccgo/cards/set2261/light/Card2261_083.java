package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_083;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: 
 * Title: Mon Calamari Dockyards (Virtual Block 6)
 * Original version of 1261_83: the Virtual Block 6.1 release sheet (2009-09-04) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "are deploy -2" for "may deploy –2"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_083 extends Card1261_083 {
    public Card2261_083() {
        super();
        setGameText("Deploy on table. Star Cruisers (except Home One) may deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if not able to otherwise, and are immune to attrition < 4. Capital Support is canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_83", "",
                "Virtual Block 6.1 release sheet (2009-09-04)", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
