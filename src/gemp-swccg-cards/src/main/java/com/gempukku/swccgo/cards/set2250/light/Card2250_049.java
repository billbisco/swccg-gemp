package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_049;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Plo Koon (V) (Virtual Block 5)
 * Original version of 1250_49: the text of the Virtual Block 5 release sheet (2009-12-07), card #48; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2250_1049) changes it: “regardless of Objective
 * restrictions” is written “regardless of objective restrictions”, with a small o. The Master List of 2013-04-27
 * prints the original words again; it is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every
 * publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_049 extends Card1250_049 {
    public Card2250_049() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Once per game, may deploy a weapon here from Reserve Deck; reshuffle. While piloting, may battle regardless of Objective restrictions. If a battle was just initiated here, may place your weapon here in Used Pile to make all non-vehicle weapon destiny draws -1.");
        setLegacyVersion(new LegacyVersion("1250_49", "",
                "Virtual Block 5 release sheet (2009-12-07), card #48; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
