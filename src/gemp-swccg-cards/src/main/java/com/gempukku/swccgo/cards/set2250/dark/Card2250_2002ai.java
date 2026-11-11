package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Aurra Sing (V)
 * PC-E2 version of the AI printing 1250_2ai: the Players Committee AI card image aurrasingai.gif (folder Virtual5-Dark,
 * upload date 2013-02-25), repeating the release-sheet words after the card list. Plays as the current card.
 * Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2250_2002ai extends Card1250_002 {
    public Card2250_2002ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_2ai", "PC-E2",
                "Players Committee AI card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
