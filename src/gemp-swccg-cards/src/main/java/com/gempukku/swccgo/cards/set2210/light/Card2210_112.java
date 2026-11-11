package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_112;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: Red Leader (V)
 * Original version of 1210_112: the text of the Virtual Block 1 release sheet (2009-12-07), card #112; the PC card
 * image and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "lose 1 Force, or
 * that character". The PC website card list (by 2012-07-21, PC-E1, the current card) writes "lose 1 Force. OR that
 * character" (swccg-guild/work#236: spelling, capitals and punctuation are versions; batch C1). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_112 extends Card1210_112 {
    public Card2210_112() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While piloting at Dantooine system, when opponent deploys a character to a related site, they choose: opponent must lose 1 Force, or that character may not battle or move this turn.");
        setLegacyVersion(new LegacyVersion("1210_112", "",
                "Virtual Block 1 release sheet (2009-12-07), card #112; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
