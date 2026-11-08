package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Title: So Be It, Jedi
 * Original version of 1262_11: the Virtual Block 6.2 release sheet text ("When Luke deploys", "may ▲ Rise, My Friend";
 * the PC card list, PC-E1 and the current card, misspells "dpeloys" and spells out the ▲). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_011 extends Card1262_011 {
    public Card2262_011() {
        setGameText("Deploy on table. When Luke deploys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may ▲ Rise, My Friend. If this Effect canceled by opponent, stack it on Insignificant Rebellion.");
        setLegacyVersion(new LegacyVersion("1262_11", "",
                "Virtual Block 6.2 release sheet (2009-12-07), card #2-11", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
