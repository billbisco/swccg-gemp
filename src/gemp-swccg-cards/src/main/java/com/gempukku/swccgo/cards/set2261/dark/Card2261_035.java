package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_035;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: He's No Jedi
 * Original version of 1261_35: the Virtual Block 6.1 release sheet text (Sep 2009), without the PC card list's
 * "or 'swing' more than once per battle" (PC-E1, the current card). Gameplay test:
 * Card_1261_035_Tests.originalVersionAddsNoSwingLimitAfterDeal.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_035 extends Card1261_035 {
    public Card2261_035() {
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If This Deal Is Getting Worse All The Time on table, for remainder of game lightsabers may not add to Force drains. Cloud City Occupation is canceled.");
        setLegacyVersion(new LegacyVersion("1261_35", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #35", LocalDate.of(2009, 9, 4), LocalDate.of(2010, 5, 29)));
    }
}
