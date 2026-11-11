package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_127;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Leia Of Alderaan (V)
 * Original version of 1220_127: the text of the Virtual Block 2 release sheet (2009-12-07), card #127; the PC card
 * image and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "Dark Side
 * Republic characters". The PC website card list (by 2012-07-21, PC-E1, the current card) writes "Dark side" with a
 * small s (swccg-guild/work#236: spelling, capitals and punctuation are versions; batch C1). Same play. Anything Goes
 * only (docs/legacy-versions.md).
 */
public class Card2220_127 extends Card1220_127 {
    public Card2220_127() {
        super();
        setGameText("Deploy on Leia. At same location, your unique (•) Rebels are deploy -1 and their immunity to attrition is +1. Dark Side Republic characters are deploy +2 and forfeit -2. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_127", "",
                "Virtual Block 2 release sheet (2009-12-07), card #127; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
