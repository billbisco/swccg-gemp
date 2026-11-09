package com.gempukku.swccgo.cards.set2200.light;

import com.gempukku.swccgo.cards.set1200.light.Card1200_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Shields
 * Type: Defensive Shield
 * Title: Chasm
 * Original version of 1200_7 (PC website card list, by 2012-07-21; the first release sheet is not found): "your total battle destiny = 1".
 * The Master List (2013-04-27, E mark) and the Virtual Shields sheet (May 2013), PC-E1, say "use 1 for your total battle destiny instead". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2200_007 extends Card1200_007 {
    public Card2200_007() {
        super();
        setGameText("Plays on table. Substituted battle destinies do not count against battle destiny limits. If all your battle destinies were canceled (and not redrawn) your total battle destiny = 1. Once per game, may play a Defensive Shield from under your Starting Effect.");
        setLegacyVersion(new LegacyVersion("1200_7", "",
                "PC website card list (2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
