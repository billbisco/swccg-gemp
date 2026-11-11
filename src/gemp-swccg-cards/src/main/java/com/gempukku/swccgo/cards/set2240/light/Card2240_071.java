package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_071;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Lars' Protocol Droid
 * Original version of 1240_71: the text of the Virtual Block 4 release sheet (2009-12-07), card #69; the PC card image
 * and the Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "droid." with the full
 * stop inside the quote. The PC website card list (by 2012-07-21, PC-E1, the current card) puts it outside ("droid'.")
 * (swccg-guild/work#236: spelling, capitals and punctuation are versions; batch C1). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2240_071 extends Card1240_071 {
    public Card2240_071() {
        super();
        setGameText("'Device' on Harvest may be treated as '[A New Hope] or [Hoth] droid.' While at a farm: your characters present are forfeit +3; may raise this site (if converted) to the top; and, once per move phase, may put hand in Used Pile to place a 'crop' on Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1240_71", "",
                "Virtual Block 4 release sheet (2009-12-07), card #69; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
