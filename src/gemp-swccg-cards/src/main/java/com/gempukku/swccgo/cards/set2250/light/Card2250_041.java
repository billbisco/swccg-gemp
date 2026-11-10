package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_041;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Alien
 * Title: Jedi Survivor
 * Original version of 1250_41: the Virtual Block 5 release sheet (official text 2009-12-07, slip 41; sheet copy re-made
 * 2010-04-01) prints destiny 1. The PC card image and the Virtual Cards Master List (official text 2013-04-27) print
 * destiny 3 with the same game text (PC-E1, dated by the Master List; the card image is undated). Bill: stat-only
 * changes count as versions. Plays as the current card except for its destiny number. Anything Goes only
 * (docs/legacy-versions.md). This id was an icon-loss alias of 1250_41 from r18 to legacy batch B34.
 */
public class Card2250_041 extends Card1250_041 {
    public Card2250_041() {
        super(1);
        setLegacyVersion(new LegacyVersion("1250_41", "",
                "Virtual Block 5 release sheet (2009-12-07), card #41, destiny 1; superseded by destiny 3 (PC forum errata, 2010-07-27)",
                LocalDate.of(2009, 12, 7), LocalDate.of(2010, 7, 27)));
    }
}
