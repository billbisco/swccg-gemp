package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_249;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Starship
 * Title: Obi-Wan In Radiant VII
 * Original version of 1250_83: the text of the Virtual Block 5 release sheet (2009-12-07), card #83; the PC card image
 * and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "may not 'cloak'" and
 * the title "Obi-Wan In Radiant VII". The PC website card list (by 2012-07-21, PC-E1, the current card) drops the
 * quote marks ("may not cloak") and prints the title "Obi-Wan in Radiant VII" (swccg-guild/work#236: spelling,
 * capitals and punctuation are versions; batch C1). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_083 extends Card601_249 {
    public Card2250_083() {
        super();
        setGameText("May add 1 pilot. Permanent pilot is •Obi-Wan, who provides ability of 6. Opponent's starships may not 'cloak'. Your total ability here may not be reduced.");
        setLegacyVersion(new LegacyVersion("1250_83", "",
                "Virtual Block 5 release sheet (2009-12-07), card #83; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
        setDisplayTitle("Obi-Wan In Radiant VII"); // the current card shows the card-list title "Obi-Wan in Radiant VII"
    }
}
