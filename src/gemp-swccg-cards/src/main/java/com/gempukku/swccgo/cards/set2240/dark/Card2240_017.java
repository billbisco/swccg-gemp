package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_017;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Creature
 * Subtype: Force Eater
 * Title: Thrawn's Ysalamir
 * Original version of 1240_17: the Virtual Block 4 release sheet printed this card as "Thrawn's Pet". The Players
 * Committee renamed it Thrawn's Ysalamir; the game text did not change. The rename is not in the PC's "New Persona
 * Changes" list of 2013-01-29; the card list shows it by 2013-03-15. Bill (2026-11-09): a title change is a version.
 * Same card and persona as the current printing (Display Title is display only). Anything Goes only (docs/legacy-
 * versions.md).
 */
public class Card2240_017 extends Card1240_017 {
    public Card2240_017() {
        super();
        setLegacyVersion(new LegacyVersion("1240_17", "",
                "Virtual Block 4 release sheet (2009-12-07), card #16, printed as Thrawn's Pet; renamed Thrawn's Ysalamir (PC card list by 2013-03-15, still Thrawn's Pet on 2013-01-11; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        setDisplayTitle("Thrawn's Pet");
    }
}
