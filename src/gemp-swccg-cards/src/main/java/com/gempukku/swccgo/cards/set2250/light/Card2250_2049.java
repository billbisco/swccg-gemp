package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_049;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Plo Koon (V) (Virtual Block 5)
 * PC-E2 version of 1250_49. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2250_49) = the Virtual Block 5 release sheet (2009-12-07), card #48; PC-E1
 * (2250_1049) = the PC website card list (by 2012-07-21): “regardless of Objective restrictions” is written
 * “regardless of objective restrictions”, with a small o. PC-E2 (2250_2049) = the PC card image (uploaded 2013-02-25;
 * it may have been made earlier), the original words again; PC-E3 = the Master List (2013-04-27), the original words
 * again, the current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2049 extends Card1250_049 {
    public Card2250_2049() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Once per game, may deploy a weapon here from Reserve Deck; reshuffle. While piloting, may battle regardless of Objective restrictions. If a battle was just initiated here, may place your weapon here in Used Pile to make all non-vehicle weapon destiny draws -1.");
        setLegacyVersion(new LegacyVersion("1250_49", "PC-E2",
                "PC card image (server upload 2013-02-25; the original words again); superseded by the Master List (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
