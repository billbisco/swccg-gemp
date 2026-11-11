package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Defensive Shield
 * Title: Leave Them To Me (V)
 * Original version of 1261_21 (the Reflections III printing): the Virtual Block 6.1 release sheet (2009-09-04; revised
 * copy 2010-04-20, slip 22), "Subjugated planet". The PC website card list (2012-07-21 to 2013-02-12) prints "Subjugated
 * Planet" (PC-E1, 2261_1021; capitals are versions). The Master List (2013-04-27) and Virtual Shields sheet (2013-05-21)
 * print these words again (PC-E2, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_021 extends Card1261_021 {
    public Card2261_021() {
        setGameText("Plays on table. At opponent's Dantooine and Subjugated planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition.");
        setLegacyVersion(new LegacyVersion("1261_21", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #22", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
