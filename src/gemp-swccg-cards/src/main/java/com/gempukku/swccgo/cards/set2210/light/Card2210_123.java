package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_123;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Hidden Fortress
 * Original version of 1210_123: the text of the Virtual Block 1 release sheet (2009-12-07), card #126E; the PC card
 * image and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "on table: once
 * per game". The PC website card list (by 2012-07-21, PC-E1, the current card) writes "Once" with a capital O
 * (swccg-guild/work#236: spelling, capitals and punctuation are versions; batch C1). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2210_123 extends Card1210_123 {
    public Card2210_123() {
        super();
        setGameText("Deploy on table (only at start of game) with up to three systems from outside your deck face-down under here. If non-[Virtual Block 4] Hidden Base on table: once per game, may deploy a system from here and/or exchange Hidden Base indicator with a system (from parsec 1 to 8) under here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_123", "",
                "Virtual Block 1 release sheet (2009-12-07), card #126E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
