package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Defensive Shield
 * Title: Leave Them To Me (Death Star II) (V)
 * Original version of 1261_24 (whose current printing shares 1261_21's class): the first copy of the Virtual Block 6.1
 * sheet (official text as of 2009-09-04), full text with "Subjugated planet". The revised sheet (2010-04-20) printed the
 * first sentence only (PC-E1, 2261_24); the PC website card list (2012-07-21 to 2013-02-12) the full text with
 * "Subjugated Planet" (PC-E2, 2261_2024). The 2013 Master List and Virtual Shields sheet print these words again (PC-E3,
 * the current card; every published text is a version, Bill 2026-11-11). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1024 extends Card1261_021 {
    public Card2261_1024() {
        setGameText("Plays on table. At opponent's Dantooine and Subjugated planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition.");
        setLegacyVersion(new LegacyVersion("1261_24", "",
                "Virtual Block 6.1 release sheet, first copy (2009-09-04), card #23", LocalDate.of(2009, 9, 4), LocalDate.of(2010, 4, 20)));
    }
}
