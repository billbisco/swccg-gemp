package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_072_BACK;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Objective
 * Title: Imperial Occupation / Imperial Control (back)
 *
 * Back side of the PC-E1 version of 1220_72 (see {@link Card2220_1072}): the 2012-09-21 errata already dropped "At marker
 * sites, AT-ATs move for free.", so this side has the current text (the 2012-11-19 errata changed only the front).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1072_BACK extends Card1220_072_BACK {
    public Card2220_1072_BACK() {
        super();
        setLegacyVersion(new LegacyVersion("1220_72_BACK", "PC-E1",
                "PC forum \"Current Rulings/Errata\" (2012-09-21, Competitive Balance Team errata)",
                LocalDate.of(2012, 9, 21), LocalDate.of(2012, 11, 19)));
    }
}
