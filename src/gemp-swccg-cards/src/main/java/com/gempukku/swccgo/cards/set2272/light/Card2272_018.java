package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.cards.set1272.light.Card1272_018;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Alien
 * Title: Tarfful, Wookiee Insurgent
 * Original version of 1272_18: the Virtual Block 7.2 release sheet printed this card as "Kashyyyk Insurgent Leader".
 * The Players Committee renamed it Tarfful, Wookiee Insurgent; the game text did not change. The slip lore is
 * "Wookiee. “GHRRRRAARRRRHG!”"; the Master List lore is "Leader." ("Leader" was in the old title, so the leader
 * keyword is the same on both). Bill (2026-11-09): a title change is a version. Same card and persona as the current
 * printing (printed title is display only). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2272_018 extends Card1272_018 {
    public Card2272_018() {
        super();
        setLore("Wookiee. \"GHRRRRAARRRRHG!\"");
        setLegacyVersion(new LegacyVersion("1272_18", "",
                "Virtual Block 7.2 release sheet (2010-12-15), card #03, printed as Kashyyyk Insurgent Leader; renamed Tarfful, Wookiee Insurgent (PC \"New Persona Changes\" 2013-01-29; PC card list by 2013-03-14; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2010, 12, 15), LocalDate.of(2013, 4, 27)));
        setPrintedTitle("Kashyyyk Insurgent Leader");
    }
}
