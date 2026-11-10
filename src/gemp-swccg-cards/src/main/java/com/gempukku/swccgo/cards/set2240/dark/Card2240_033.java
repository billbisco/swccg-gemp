package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_193;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Kuat Drive Yards (V)
 * Original version of 1240_33: the Virtual Block 4 release sheet text (slip 31E, 2009-12-07; the PC card image and the
 * Master List of 2013-04-27 print it, the Master List copy being a stale reprint), "Imperial-class". The PC website card
 * list (by 2012-07-21, PC-E1, the current card) writes "Imperial-Class" with a capital C (swccg-guild/work#236: capitals
 * are versions). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_033 extends Card601_193 {
    public Card2240_033() {
        super();
        setGameText("Deploy on table. Unique (•) Imperial-class Star Destroyers draw one battle destiny if not able to otherwise and are immune to attrition < 4. During your deploy phase, may use 2 Force to [download] a battleground system. We're In Attack Position Now is canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_33", "",
                "Virtual Block 4 release sheet (2009-12-07); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
