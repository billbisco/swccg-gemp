package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_175;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Bravo Fighter (V)
 * PC-E1 version of 1250_80: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1080 extends Card601_175 {
    public Card2250_1080() {
        super();
        setGameText("May add one astromech. During battle, may target a starship with no character piloting. Draw destiny. If destiny > defense value, target hit. Permanent pilot provides ability of 4. Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1250_80", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
