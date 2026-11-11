package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_027;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Interrupt
 * Title: Sonic Bombardment (V)
 * PC-E2 version of 1281_17: the Virtual Cards Master List (2013-04-27), which repeats the 2012 release-sheet words
 * (no comma after "Once per game"). Superseded by the PC card image (PC-E3, folder upload date 2013-05-07), the
 * current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2017 extends Card601_027 {
    public Card2281_2017() {
        super();
        setGameText("Once per game ▲ a prison. OR Use 1 Force to ▲ a non-droid assassin (or slaver) of ability < 5. OR During battle involving an assassin, opponent's battle destiny draws are -1. OR Cancel opponent's attempt to modify the power of a starfighter piloted by your alien.");
        setLegacyVersion(new LegacyVersion("1281_17", "PC-E2",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
