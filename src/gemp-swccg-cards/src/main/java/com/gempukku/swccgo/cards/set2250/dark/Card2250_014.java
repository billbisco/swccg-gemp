package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_014;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: Expand The Empire (V)
 * Original version of 1250_14: the Virtual Block 5 release sheet text (the PC card list, PC-E1, reorders the
 * list of cards that are lost). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_014 extends Card1250_014 {
    public Card2250_014() {
        setGameText("Deploy on Senate. May raise your converted Senate to the top. Jedi here are politics +4. While your total politics here is > 5, at battlegrounds where you have an Imperial, your Force drains and battle destiny draws are +1. Your Maul, aliens (except Mara), [Independent Starship] starships, and senators are lost. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_14", "",
                "Virtual Block 5 release sheet (2009-12-07), card #18", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
