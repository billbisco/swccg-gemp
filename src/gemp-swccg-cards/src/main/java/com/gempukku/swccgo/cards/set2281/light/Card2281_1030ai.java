package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Supreme Chancellor Valorum (V) (AI)
 * PC-E1 version of the AI printing 1281_30ai: the PC website card-list text (its own row for the AI card), the same
 * text and play as 2281_1030 (the card-list version of 1281_30); its own card for the AI image. The Master List
 * (2013-04-27, PC-E2, the current card) repeats the release-sheet text. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1030ai extends Card2281_1030 {
    public Card2281_1030ai() {
        super();
        setLegacyVersion(new LegacyVersion("1281_30ai", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
