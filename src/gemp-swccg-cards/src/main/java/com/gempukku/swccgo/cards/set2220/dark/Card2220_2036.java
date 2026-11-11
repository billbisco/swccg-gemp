package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: I'm Sorry
 *
 * PC-E2 version of 1220_36: the Competitive Balance Team errata posted on the Players Committee forum (p841760,
 * 2013-10-09), "... or an Obsidian squadron TIE on table from Reserve Deck ... if you control 2 cloud sectors with
 * TIEs ...". Plays like the current card (any cloud sector to Bespin). Superseded by the Master List wording
 * (PDF built February 2014). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2036 extends Card1220_036 {
    public Card2220_2036() {
        super();
        setGameText("Deploy on table. Floating Refinery is suspended during opponent's turn. During your deploy phase, may deploy a Floating Refinery, a cloud sector (to Bespin), or an Obsidian squadron TIE on table from Reserve Deck; reshuffle. During your turn, if you control 2 cloud sectors with TIEs, opponent loses 1 Force. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_36", "PC-E3",
                "Competitive Balance Team errata, PC forum p841760 (2013-10-09); superseded by the Players Committee Virtual Cards Master List (PDF built February 2014)", LocalDate.of(2013, 10, 9), LocalDate.of(2014, 2, 3)));
    }
}
