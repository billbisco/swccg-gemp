package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_175;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Bravo Fighter (V)
 *
 * PC-E2 version of 1250_80: the PC card image (bravofighter.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2080 extends Card601_175 {
    public Card2250_2080() {
        super();
        setGameText("Permanent pilot provides ability of 4. May add 1 astromech. During battle, may target a starship with no character piloting. Draw destiny. If destiny > defense value, target hit. Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1250_80", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
