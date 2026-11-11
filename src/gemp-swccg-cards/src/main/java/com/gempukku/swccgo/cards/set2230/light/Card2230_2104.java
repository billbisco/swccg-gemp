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
 * PC-E2 version of 1230_104: the PC card image (endorcelebration.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2104 extends Card1230_104 {
    public Card2230_2104() {
        super();
        setGameText("Cancel an Effect with 'occupation' in title. (Immune to Sense.) OR During your draw phase, if you have a Rebel scout alone at Back Door, draw destiny. If destiny < the number of your scouts on table, activate or retrieve 5 Force and place this Interrupt on bottom of Lost Pile.");
        setLegacyVersion(new LegacyVersion("1230_104", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
