package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_067;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Krayt Dragon Howl (V)
 * Original version of 1250_67: the text of the Virtual Block 5 release sheet (2009-12-07), card #67E; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2250_1067) changes it: “Obi-Wan's Cape” is written
 * “Obi-wan's Cape”, with a small w. The Master List of 2013-04-27 prints the original words again; it is listed as
 * its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2250_067 extends Card1250_067 {
    public Card2250_067() {
        super();
        setGameText("USED: Activate 1 Force. STARTING: If Credits Will Do Fine on table, deploy from Reserve Deck Queen's Landing Site (with [Tatooine] Obi-Wan, his matching weapon, and [Virtual Block 1] Obi-Wan's Cape there). When you draw your starting hand, draw five cards instead of eight. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1250_67", "",
                "Virtual Block 5 release sheet (2009-12-07), card #67E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
