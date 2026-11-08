package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_042;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Lone Pilot (V)
 * PC-E2 version of 1261_42: the Virtual Cards Master List text (2013-04-27), Juno Eclipse; same play as the current card (whose GEMP wording is not a published text).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2042 extends Card1261_042 {
    public Card2261_2042() {
        setGameText("USED: Activate 1 Force. STARTING: ▲ Bossk, Dengar, DS-61-2, Guri, or Juno Eclipse (may also ▲ a system with exactly 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect.) When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_42", "PC-E2",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), null));
    }
}
