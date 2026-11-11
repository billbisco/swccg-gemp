package com.gempukku.swccgo.cards.set2262.light;

import com.gempukku.swccgo.cards.set1262.light.Card1262_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Card
 * Title: At Peace (V) (Virtual Block 6)
 * PC-E1 version of 1262_22: the PC website card list text (by 2012-07-21; Virtual Block 6 list and Virtual master
 * list to 2013-01-11). Against the original (2262_22): “Broken Concentration” is written “Broken concentration”, with
 * a small c. The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again
 * (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2262_1022 extends Card1262_022 {
    public Card2262_1022() {
        super();
        setGameText("Deploy on Yoda's Hut. May use 1 Force to prevent Broken concentration from modifying your training destiny (and Strong Is Vader from retrieving Force) this turn. If Luke has completed Jedi Test #5 and is about to be hit, may place this Effect out of play to prevent it.");
        setLegacyVersion(new LegacyVersion("1262_22", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
