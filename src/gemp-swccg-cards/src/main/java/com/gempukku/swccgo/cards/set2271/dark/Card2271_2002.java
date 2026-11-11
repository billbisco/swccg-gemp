package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Title: Darth Vader, More Machine Than Man
 * PC-E1 version of 1271_2 (Batch S1, Bill 2026-11-11: every published text is a version): the PC forum errata of
 * 2011-03-21 ("May not be Disarmed." before the immunity). The same words return as PC-E3, the current card (Master List
 * 2013-04-27 and PC card image), after the card list's PC-E2 (2271_1002). Same text and play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2002 extends Card1271_002 {
    public Card2271_2002() {
        setLegacyVersion(new LegacyVersion("1271_2", "PC-E1",
                "PC forum errata (2011-03-21, Current Rulings/Errata p608385)", LocalDate.of(2011, 3, 21), LocalDate.of(2012, 7, 21)));
    }
}
