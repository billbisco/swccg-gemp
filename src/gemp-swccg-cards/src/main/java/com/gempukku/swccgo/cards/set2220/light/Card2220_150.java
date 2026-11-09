package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_150;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Title: NOOOOOOOOOOOO!
 * Original version of 1220_150: the Virtual Block 2 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "during a battle" for "during battle"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_150 extends Card1220_150 {
    public Card2220_150() {
        super();
        setGameText("Cancel Broken Concentration, The Circle Is Now Complete, or a duel initiated during battle. (Immune to Young Fool.) OR During any draw phase, place a card from hand under your Used Pile to take any one card into hand from Force Pile; reshuffle.");
        setLegacyVersion(new LegacyVersion("1220_150", "",
                "Virtual Block 2 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
