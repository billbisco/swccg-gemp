package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_067;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Krayt Dragon Howl (V)
 * PC-E1 version of 1250_67. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2250_67) = the Virtual Block 5 release sheet (2009-12-07), card #67E; PC-E1
 * (2250_1067) = the PC website card list (by 2012-07-21): “Obi-Wan's Cape” is written “Obi-wan's Cape”, with a small
 * w. PC-E2 (2250_2067) = the PC card image (uploaded 2013-02-25; it may have been made earlier), the original words
 * again; PC-E3 = the Master List (2013-04-27), the original words again, the current card. Same play. Anything Goes
 * only (docs/legacy-versions.md).
 */
public class Card2250_1067 extends Card1250_067 {
    public Card2250_1067() {
        super();
        setGameText("USED: Activate 1 Force. STARTING: If Credits Will Do Fine on table, deploy from Reserve Deck Queen's Landing Site (with [Tatooine] Obi-Wan, his matching weapon, and [Virtual Block 1] Obi-wan's Cape there). When you draw your starting hand, draw five cards instead of eight. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1250_67", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the PC card image (uploaded 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
