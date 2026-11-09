package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_112;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Starship
 * Subtype: Starfighter
 * Title: Infinity
 * Original version of 1240_112: the Virtual Block 4 release sheet printed this card as "BoShek's Modified Light
 * Freighter". The Players Committee renamed it Infinity; the game text did not change. The slip prints lore; the
 * Master List removed it. Bill (2026-11-09): a title change is a version. Same card and persona as the current
 * printing (printed title is display only). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_112 extends Card1240_112 {
    public Card2240_112() {
        super();
        setLore("Modern Corellian design. Length 100 meters. Features engine system similar to that of a Corellian corvette. Dorsal hatch reveals hangar bay.");
        setLegacyVersion(new LegacyVersion("1240_112", "",
                "Virtual Block 4 release sheet (2009-12-07), card #109E, printed as BoShek's Modified Light Freighter; renamed Infinity (PC \"New Persona Changes\" 2013-01-29; PC card list by 2013-03-15; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        setPrintedTitle("BoShek's Modified Light Freighter");
    }
}
