package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_044;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Title: Master Qui-Gon (V)
 * PC-E1 version of 1250_44 (Batch S1, Bill 2026-11-11): the PC forum errata of 2011-03-21, "Immune to You Are Beaten, and
 * attrition." (with the comma). The same words return as PC-E4, the current card (Master List 2013-04-27). Same text and
 * play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2044 extends Card1250_044 {
    public Card2250_2044() {
        setLegacyVersion(new LegacyVersion("1250_44", "PC-E1",
                "PC forum errata (2011-03-21, Current Rulings/Errata p608385)", LocalDate.of(2011, 3, 21), LocalDate.of(2012, 7, 21)));
    }
}
