package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Ingenuity
 * Original version of 1281_36 (Virtual Block 8.1 release sheet), before the PC website card list rephrase (2012-07-21, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_036 extends Card1281_036 {
    public Card2281_036() {
        super();
        setGameText("Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel is power +2 and his total weapon destiny is +1. During your control phase, may draw destiny. If destiny > 3, your Force drains at same and related locations are +1 for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1281_36", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
