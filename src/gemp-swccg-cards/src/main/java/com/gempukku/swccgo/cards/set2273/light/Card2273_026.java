package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Title: Luke Skywalker, Rebel Hero
 * Original version of 1273_26: the text of the Virtual Block 7.3 release sheet (2011-06-03), card #01; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2273_1026) changes it: three changes: the comma
 * after “When deployed” is dropped (“When deployed, may” becomes “When deployed may”), “lose 1 Force” is written
 * “lose 1 force”, and “from Lost Pile” is written “from lost pile”, in small letters. The Master List of 2013-04-27
 * prints the original words again; it is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every
 * publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_026 extends Card1273_026 {
    public Card2273_026() {
        super();
        setGameText("Adds 3 to the power of anything he pilots. When deployed, may deploy from Reserve Deck (or lose 1 Force to deploy from Lost Pile) a blaster on Luke; reshuffle. While armed with a blaster, it may not be stolen and characters hit by Luke may not be used to satisfy attrition. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1273_26", "",
                "Virtual Block 7.3 release sheet (2011-06-03), card #01; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21)));
    }
}
