package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_104;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Endor Celebration
 *
 * PC-E1 version of 1230_104: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_1104 extends Card1230_104 {
    public Card2230_1104() {
        super();
        setGameText("Cancel an Effect with 'occupation' in title. (Immune to Sense.) OR During your draw phase, if you have a Rebel scout alone at Back Door, draw destiny. Activate or retrieve 5 Force and place this Interrupt on bottom of Lost Pile if destiny < the number of your scouts on table.");
        setLegacyVersion(new LegacyVersion("1230_104", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
