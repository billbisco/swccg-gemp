package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Title: Het Nkik (V)
 * PC-E1 version of 1281_29: the PC website card list text from 2012-07-21 (Virtual Block 8 list until it changed by
 * 2012-12-12; Virtual master list to 2013-01-11): "may not be cancelled", "where X = the number of deserts". The
 * Virtual Block 8 list from 2012-12-12 spells "canceled" (PC-E2, the current card); the two lists overlap
 * (swccg-guild/work#236). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1029 extends Card2281_029 {
    public Card2281_1029() {
        super();
        setGameText("Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be cancelled. During your control phase, may draw X cards from Reserve Deck, where X = the number of deserts you control with a Jawa.");
        setLegacyVersion(new LegacyVersion("1281_29", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2012, 12, 12)));
    }
}
