package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_104;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Endor Celebration (V)
 * Original version of 1230_104: the Virtual Block 3 release sheet text (Dec 2009), "If destiny < the number of your scouts on
 * table, activate or retrieve 5 Force ...". The PC website card list (by Jul 2012) moved the condition to the end of the
 * sentence. Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_104 extends Card1230_104 {
    public Card2230_104() {
        super();
        setGameText("Cancel an Effect with 'occupation' in title. (Immune to Sense.) OR During your draw phase, if you have a Rebel scout alone at Back Door, draw destiny. If destiny < the number of your scouts on table, activate or retrieve 5 Force and place this Interrupt on bottom of Lost Pile.");
        setLegacyVersion(new LegacyVersion("1230_104", "",
                "Virtual Block 3 release sheet (2009-12-07), card #104", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
