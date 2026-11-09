package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_042;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Lone Pilot (V)
 * PC-E1 version of 1261_42: the revised Virtual Block 6.1 sheet text (2010-04-20), "exactly 1", still naming Black Leader (the Juno persona); same play as the current card. Superseded by the PC card list text (by 2012-07-21, 2261_2042, PC-E2: "a matching starship" moved before the system), then by the Master List (PC-E3, the current card; legacy batch B16).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1042 extends Card1261_042 {
    public Card2261_1042() {
        setGameText("USED: Activate 1 Force. STARTING: ▲ Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also ▲ a system with exactly 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect.) When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_42", "PC-E1",
                "Virtual Block 6.1 release sheet, revised (2010-04-20), card #45; superseded by the PC website card list text (by 2012-07-21)", LocalDate.of(2010, 4, 20), LocalDate.of(2012, 7, 21)));
    }
}
