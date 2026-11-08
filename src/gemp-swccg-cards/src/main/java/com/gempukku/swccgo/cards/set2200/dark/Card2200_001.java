package com.gempukku.swccgo.cards.set2200.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Block D
 * Type: Effect
 * Title: Firepower (V)
 * Original version of 1200_1 (PC website card list, by 2012-07-21): "you may retrieve 1 Force", before the Virtual Shields sheet rephrase (May 2013, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2200_001 extends Card601_036 {
    public Card2200_001() {
        super();
        setGameText("Plays on table. If opponent moves from a location you occupy during your turn, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and system) and opponent deployed a card with ability and did not initiate a battle, you may retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1200_1", "", "PC website card list (2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 5, 21)));
    }
}
