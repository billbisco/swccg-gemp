package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_067;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Krayt Dragon Howl (V)
 * PC-E1 version of 1250_67: the PC website card list text (by 2012-07-21; Virtual Block 5 list and Virtual master
 * list to 2013-01-11). Against the original (2250_67): “Obi-Wan's Cape” is written “Obi-wan's Cape”, with a small w.
 * The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again (swccg-guild/work#236; Bill
 * 2026-11-11: every publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1067 extends Card1250_067 {
    public Card2250_1067() {
        super();
        setGameText("USED: Activate 1 Force. STARTING: If Credits Will Do Fine on table, deploy from Reserve Deck Queen's Landing Site (with [Tatooine] Obi-Wan, his matching weapon, and [Virtual Block 1] Obi-wan's Cape there). When you draw your starting hand, draw five cards instead of eight. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1250_67", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
