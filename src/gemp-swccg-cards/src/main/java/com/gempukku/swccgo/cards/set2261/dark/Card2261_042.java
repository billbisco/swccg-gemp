package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_042;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Lone Pilot (V)
 * Original version of 1261_42: the Virtual Block 6.1 release sheet text (Sep 2009): Black Leader (the Juno persona) and "a system with 1 [Dark Side Force]" ("exactly" added 2010-04-20). Played like the current card (a system with exactly one Dark Side icon).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_042 extends Card1261_042 {
    public Card2261_042() {
        setGameText("USED: Activate 1 Force. STARTING: ▲ Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also ▲ a system with 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect.) When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_42", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #45", LocalDate.of(2009, 9, 4), LocalDate.of(2010, 4, 20)));
    }
}
