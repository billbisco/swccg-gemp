package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_027;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Interrupt
 * Title: Sonic Bombardment (V)
 * PC-E1 version of 1281_17: the PC website card list text (by 2012-07-21), with the arrows written out and "starship"
 * for "starfighter". Plays as the current card in GEMP (the last option is not implemented). Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2281_1017 extends Card601_027 {
    public Card2281_1017() {
        super();
        setGameText("Once per game, may take into hand a prison from Reserve Deck; reshuffle. OR Use 1 Force to take into hand a non-droid assassin or slaver of ability < 5 from Reserve Deck; reshuffle. OR During battle involving an assassin, each of opponent's battle destiny draws is -1. OR Cancel opponent's attempt to modify the power of a starship piloted by your alien.");
        setLegacyVersion(new LegacyVersion("1281_17", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
