package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Subtype: Immediate
 * Title: He Is Not Ready (V)
 * Original version of 1240_26: the Virtual Block 4 release sheet (official text as of 2009-12-07; the surviving copy
 * was re-made 2010-04-20), card #25E, printed this card as "He Is Not Ready & Imperial Propaganda". The PC errata of 2013-06-14 (rules
 * forum, Current Rulings/Errata p813434) renamed it He Is Not Ready (V); "Its gametext remains unchanged". Bill
 * (2026-11-10): the "&" title is the original, the short title the June 2013 errata. Same card and persona as the
 * current printing (Display Title is display only). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_026 extends Card1240_026 {
    public Card2240_026() {
        super();
        setLegacyVersion(new LegacyVersion("1240_26", "",
                "Virtual Block 4 release sheet (2009-12-07), card #25E, printed as He Is Not Ready & Imperial Propaganda; renamed He Is Not Ready (V) by the PC errata of 2013-06-14",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 6, 14)));
        setDisplayTitle("He Is Not Ready & Imperial Propaganda");
    }
}
