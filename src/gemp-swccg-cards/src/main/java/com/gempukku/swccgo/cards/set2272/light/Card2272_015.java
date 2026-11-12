package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.cards.set1272.light.Card1272_015;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Rebel
 * Title: Rahm Kota, Blind Jedi
 * Original version of 1272_15: the Virtual Block 7.2 release sheet printed this card as "Blind Jedi". The Players
 * Committee renamed it Rahm Kota, Blind Jedi; the game text did not change. Bill (2026-11-09): a title change is a
 * version. Same card and persona as the current printing (Display Title is display only). Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2272_015 extends Card1272_015 {
    public Card2272_015() {
        super();
        setLegacyVersion(new LegacyVersion("1272_15", "",
                "Virtual Block 7.2 release sheet (2010-12-15), card #02, printed as Blind Jedi; renamed Rahm Kota, Blind Jedi (PC \"New Persona Changes\" 2013-01-29; PC card list by 2013-03-14; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2010, 12, 15), LocalDate.of(2013, 4, 27)));
        setDisplayTitle("Blind Jedi");
    }
}
