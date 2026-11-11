package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Starship
 * Subtype: Capital
 * Title: Errant Venture
 * Original version of 1281_45: the Virtual Block 8.1 release sheet (official text as of 2012-05-11; the surviving copy
 * was re-made 2012-07-18) printed this card as "•Booster's Star Destroyer (V)" with the same game text as the current
 * card; the PC website card list kept that title to 2013-01-11. Renamed Errant Venture (PC card list by 2013-03-13;
 * Virtual Cards Master List 2013-04-27). Bill (2026-11-09): title changes are versions.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_045 extends Card1281_045 {
    public Card2281_045() {
        super();
        setLegacyVersion(new LegacyVersion("1281_45", "",
                "Virtual Block 8.1 release sheet (2012-05-11), printed as Booster's Star Destroyer (the sheet heading adds \"(V)\"; the title bar has none)",
                LocalDate.of(2012, 5, 11), LocalDate.of(2013, 3, 13)));
        setPrintedTitle("Booster's Star Destroyer");
    }
}
