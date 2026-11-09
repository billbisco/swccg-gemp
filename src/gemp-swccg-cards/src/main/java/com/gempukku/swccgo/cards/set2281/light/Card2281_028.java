package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_028;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Rebel
 * Title: Kyle Katarn
 * Original version of 1281_28: the Virtual Block 8.1 release sheet (official text as of 2012-05-11; the surviving copy
 * was re-made 2012-07-18) printed this card as "•Rebel Agent" with the same game text and lore as the current card.
 * The Virtual Cards Master List (2013-04-27; PC card list by 2013-03-13) renamed it Kyle Katarn. Bill (2026-11-09):
 * title changes are versions. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_028 extends Card1281_028 {
    public Card2281_028() {
        super();
        setLegacyVersion(new LegacyVersion("1281_28", "",
                "Virtual Block 8.1 release sheet (2012-05-11), printed as Rebel Agent",
                LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
        setPrintedTitle("Rebel Agent");
    }
}
