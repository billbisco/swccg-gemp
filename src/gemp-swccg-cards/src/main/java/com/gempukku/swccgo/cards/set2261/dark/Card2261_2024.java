package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Defensive Shield
 * Title: Leave Them To Me (Death Star II) (V)
 * PC-E2 version of 1261_24: the PC website card list (2012-07-21 to 2013-02-12) printed the full text with "Subjugated
 * Planet" (capital P) after the revised sheet's short text (PC-E1, 2261_24). Superseded by the Virtual Cards Master List
 * (2013-04-27) and Virtual Shields sheet (2013-05-21), "Subjugated planet" (PC-E3, the current card). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2024 extends Card1261_021 {
    public Card2261_2024() {
        setGameText("Plays on table. At opponent's Dantooine and Subjugated Planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition.");
        setLegacyVersion(new LegacyVersion("1261_24", "PC-E2",
                "PC website card list (2012-07-21 to 2013-02-12)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
