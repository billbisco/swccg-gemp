package com.gempukku.swccgo.cards.set2262.light;

import com.gempukku.swccgo.cards.set1262.light.Card1262_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Card
 * Title: At Peace (V) (Virtual Block 6)
 * Original version of 1262_22: the text of the Virtual Block 6.2 release sheet (2009-12-07), card #22; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2262_1022) changes it: “Broken Concentration” is
 * written “Broken concentration”, with a small c. The Master List of 2013-04-27 prints the original words again; it
 * is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_022 extends Card1262_022 {
    public Card2262_022() {
        super();
        setGameText("Deploy on Yoda's Hut. May use 1 Force to prevent Broken Concentration from modifying your training destiny (and Strong Is Vader from retrieving Force) this turn. If Luke has completed Jedi Test #5 and is about to be hit, may place this Effect out of play to prevent it.");
        setLegacyVersion(new LegacyVersion("1262_22", "",
                "Virtual Block 6.2 release sheet (2009-12-07), card #22; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
