package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_102;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: Jek Porkins (V)
 * Original version of 1210_102: the text of the Virtual Block 1 release sheet (2009-12-07), card #102; the PC card
 * image and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "and, during" and
 * "X-wing". The PC website card list (by 2012-07-21, PC-E1, the current card) drops the comma after "and" and writes
 * "X-Wing" (swccg-guild/work#236: spelling, capitals and punctuation are versions; batch C1). Same play. Anything Goes
 * only (docs/legacy-versions.md).
 */
public class Card2210_102 extends Card1210_102 {
    public Card2210_102() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While piloting Red 6: it is Heroic, immune to attrition < 4, Jek draws one battle destiny if unable to otherwise, and, during your control phase, may use 1 Force to retrieve a Rogue squadron pilot or an X-wing.");
        setLegacyVersion(new LegacyVersion("1210_102", "",
                "Virtual Block 1 release sheet (2009-12-07), card #102; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
