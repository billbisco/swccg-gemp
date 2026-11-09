package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Alien
 * Title: Jango Fett
 * Original version of 1250_8: the Virtual Block 5 release sheet printed this card as "The Mandalorian". The Players
 * Committee renamed it Jango Fett; the game text did not change. Bill (2026-11-09): a title change is a version. Same
 * card and persona as the current printing (printed title is display only). Anything Goes only (docs/legacy-
 * versions.md).
 */
public class Card2250_008 extends Card1250_008 {
    public Card2250_008() {
        super();
        setLegacyVersion(new LegacyVersion("1250_8", "",
                "Virtual Block 5 release sheet (2009-12-07), card #11, printed as The Mandalorian; renamed Jango Fett (PC \"New Persona Changes\" 2013-01-29; PC card list by 2013-03-14; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        setPrintedTitle("The Mandalorian");
    }
}
