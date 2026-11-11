package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_080;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Jabba, This Is Your Last Chance!
 * Original version of 1261_80: the Virtual Block 6.1 release sheet text (Sep 2009), without the PC card list's
 * "or 'swing' more than once per battle" (PC-E1, the current card). Gameplay test:
 * Card_1261_080_Tests.OriginalVersionAddsNoSwingLimitEvenWhileQuietMiningColonyOnTable.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_080 extends Card1261_080 {
    public Card2261_080() {
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains. Cloud City Celebration is canceled.");
        setLegacyVersion(new LegacyVersion("1261_80", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #79", LocalDate.of(2009, 9, 4), LocalDate.of(2010, 5, 29)));
    }
}
