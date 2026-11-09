package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_027;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Block 8
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sonic Bombardment (V)
 * Original version of 1281_17 (Virtual Block 8.1 release sheet), before the PC website card list text (2012-07-21, PC-E1). Same play as the current card in GEMP. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_017 extends Card601_027 {
    public Card2281_017() {
        super();
        setGameText("Once per game ▲ a prison. OR Use 1 Force to ▲ a non-droid assassin (or slaver) of ability < 5. OR During battle involving an assassin, opponent's battle destiny draws are -1. OR Cancel opponent's attempt to modify the power of a starfighter piloted by your alien.");
        setLegacyVersion(new LegacyVersion("1281_17", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
