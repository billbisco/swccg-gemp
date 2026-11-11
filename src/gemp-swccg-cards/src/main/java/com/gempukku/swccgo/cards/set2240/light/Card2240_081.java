package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_081;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Subtype: Immediate
 * Title: Eject! Eject! (V)
 * Original version of 1240_81: the Virtual Block 4 release sheet (official text as of 2009-12-07; the surviving copy
 * was re-made 2010-04-20), card #83E, printed this card as "Eject! Eject! & Imperial Atrocity". The PC errata of 2013-06-14 (rules
 * forum, Current Rulings/Errata p813434) renamed it Eject! Eject! (V); "Its gametext remains unchanged". Bill
 * (2026-11-10): the "&" title is the original, the short title the June 2013 errata. Same card and persona as the
 * current printing (Display Title is display only). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_081 extends Card1240_081 {
    public Card2240_081() {
        super();
        setLegacyVersion(new LegacyVersion("1240_81", "",
                "Virtual Block 4 release sheet (2009-12-07), card #83E, printed as Eject! Eject! & Imperial Atrocity; renamed Eject! Eject! (V) by the PC errata of 2013-06-14",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 6, 14)));
        setDisplayTitle("Eject! Eject! & Imperial Atrocity");
    }
}
