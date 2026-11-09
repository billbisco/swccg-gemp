package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set601.light.Card601_173;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Republic
 * Title: Maris Brood, Fallen Jedi
 * Original version of 1240_66: the Virtual Block 4 release sheet printed this card as "Fallen Jedi". The Players
 * Committee renamed it Maris Brood, Fallen Jedi; the game text did not change. The slip prints lore; the Master List
 * removed it (non-gameplay lore was dropped with the persona change). Female stays a keyword on both. Bill
 * (2026-11-09): a title change is a version. Same card and persona as the current printing (printed title is display
 * only). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_066 extends Card601_173 {
    public Card2240_066() {
        super();
        setLore("With only a few Jedi remaining in the galaxy, each confrontation between them has far reaching consequences. Female.");
        setLegacyVersion(new LegacyVersion("1240_66", "",
                "Virtual Block 4 release sheet (2009-12-07), card #67, printed as Fallen Jedi; renamed Maris Brood, Fallen Jedi (PC \"New Persona Changes\" 2013-01-29; PC card list by 2013-03-15; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        setPrintedTitle("Fallen Jedi");
    }
}
