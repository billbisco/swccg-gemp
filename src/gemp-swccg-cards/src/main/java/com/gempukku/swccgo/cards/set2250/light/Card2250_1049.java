package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_049;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Plo Koon (V) (Virtual Block 5)
 * PC-E1 version of 1250_49: the PC website card list text (by 2012-07-21; Virtual Block 5 list and Virtual master
 * list to 2013-01-11). Against the original (2250_49): “regardless of Objective restrictions” is written “regardless
 * of objective restrictions”, with a small o. The Master List of 2013-04-27 (PC-E2, the current card) prints the
 * original words again (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2250_1049 extends Card1250_049 {
    public Card2250_1049() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Once per game, may deploy a weapon here from Reserve Deck; reshuffle. While piloting, may battle regardless of objective restrictions. If a battle was just initiated here, may place your weapon here in Used Pile to make all non-vehicle weapon destiny draws -1.");
        setLegacyVersion(new LegacyVersion("1250_49", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
