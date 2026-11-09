package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_009;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Alien
 * Title: Moruth Doole, Kessel Administrator
 * Original version of 1273_9: the Virtual Block 7.3 release sheet (official text as of 2011-06-03) printed this card as
 * "•Spice Mine Administrator" (no lore), and the PC website card list kept that title to 2013-01-11. The Players
 * Committee renamed it Moruth Doole, Kessel Administrator (PC card list by 2013-03-14; Virtual Cards Master List,
 * official text 2013-04-27). The game text never changed. Bill (2026-11-09): title changes are versions.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_009 extends Card1273_009 {
    public Card2273_009() {
        super();
        setLegacyVersion(new LegacyVersion("1273_9", "",
                "Virtual Block 7.3 release sheet (2011-06-03), printed as Spice Mine Administrator; renamed Moruth Doole, Kessel Administrator (PC card list by 2013-03-14, Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2011, 6, 3), LocalDate.of(2013, 3, 14)));
        setPrintedTitle("Spice Mine Administrator");
    }
}
