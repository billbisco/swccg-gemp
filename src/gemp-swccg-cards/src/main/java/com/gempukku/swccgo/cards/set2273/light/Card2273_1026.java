package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Title: Luke Skywalker, Rebel Hero
 * PC-E1 version of 1273_26. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2273_26) = the Virtual Block 7.3 release sheet (2011-06-03), card #01; PC-E1
 * (2273_1026) = the PC website card list (by 2012-07-21): three changes: the comma after “When deployed” is dropped
 * (“When deployed, may” becomes “When deployed may”), “lose 1 Force” is written “lose 1 force”, and “from Lost Pile”
 * is written “from lost pile”, in small letters. PC-E2 (2273_2026) = the PC card image (uploaded 2013-02-25; it may
 * have been made earlier), the original words again; PC-E3 = the Master List (2013-04-27), the original words again,
 * the current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1026 extends Card1273_026 {
    public Card2273_1026() {
        super();
        setGameText("Adds 3 to the power of anything he pilots. When deployed may deploy from Reserve Deck (or lose 1 force to deploy from lost pile) a blaster on Luke; reshuffle. While armed with a blaster, it may not be stolen and characters hit by Luke may not be used to satisfy attrition. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1273_26", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the PC card image (uploaded 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
