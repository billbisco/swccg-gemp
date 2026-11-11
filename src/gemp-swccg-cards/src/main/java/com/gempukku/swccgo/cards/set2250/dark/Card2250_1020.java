package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Card
 * Title: Order 66
 * PC-E1 version of 1250_20: the PC website card list text (by 2012-07-21; Virtual Block 5 list and Virtual master
 * list to 2013-01-11). Against the original (2250_20): “Expand The Empire” is written “Expand the Empire”, with a
 * small t. The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again
 * (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2250_1020 extends Card1250_020 {
    public Card2250_1020() {
        super();
        setGameText("Deploy on Sidious. If with [Virtual Block 5] Expand the Empire during your control phase: relocate this card to an [Episode I] Jedi at a battleground or Senate (otherwise, retrieve 1 Force). If on a Jedi, add one battle destiny here, and if that Jedi just lost, relocate this card to Sidious.");
        setLegacyVersion(new LegacyVersion("1250_20", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
