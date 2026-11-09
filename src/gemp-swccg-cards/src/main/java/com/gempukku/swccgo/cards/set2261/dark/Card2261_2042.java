package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_042;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Lone Pilot (V)
 * PC-E2 version of 1261_42: the PC website card list text (by 2012-07-21 to 2013-01-11), which lists "a matching
 * starship" before "a system with exactly 1 [Dark Side Force]" (a reorder, so a version) and closes "Effect)."; the
 * list's written-out icons are shown as on the slips. Superseded by the Virtual Cards Master List (PC-E3, 2013-04-27).
 * Same play as the current card. Restored as a version by legacy batch B16 (it was aliased to 2261_1042 as an
 * icon-loss version). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2042 extends Card1261_042 {
    public Card2261_2042() {
        setGameText("USED: Activate 1 Force. STARTING: ▲ Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also ▲ a matching starship, a system with exactly 1 [Dark Side Force], and an always [Immune to Alter] Effect). When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_42", "PC-E2",
                "PC website card list (by 2012-07-21); superseded by the Virtual Cards Master List text (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
