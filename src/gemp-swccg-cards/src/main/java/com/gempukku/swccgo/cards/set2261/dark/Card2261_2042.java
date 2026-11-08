package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_042;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Lone Pilot (V)
 * PC-E2 version of 1261_42: the PC website card list text (by 2012-07-21), still naming Black Leader (the Juno
 * persona) before the 2013 persona change, with the starship listed before the system. Same play as the current card.
 * (This id held the Master List text, labelled PC-E2, until the current card took that text, PC-E3.)
 * Image: the current card image.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2042 extends Card1261_042 {
    public Card2261_2042() {
        setGameText("USED: Activate 1 Force. STARTING: Take into hand from Reserve Deck Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also take into hand from Reserve Deck a matching starship, a system with exactly 1 [Dark Side Force], and an always [Immune to Alter] Effect). When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_42", "PC-E2",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
