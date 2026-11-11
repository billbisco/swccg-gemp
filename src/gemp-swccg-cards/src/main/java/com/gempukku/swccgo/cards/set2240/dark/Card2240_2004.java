package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Imperial
 * Title: Colonel Wullf Yularen
 *
 * PC-E2 version of 1240_4: the PC card image (colonelwullfyularen.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2004 extends Card1240_004 {
    public Card2240_2004() {
        super();
        setGameText("Imperial Council Member. Agenda: logistics. ISB Agents are forfeit +1. While at Conference Room with Sim Aloo, may use 1 Force to subtract 1 from an oponent's just drawn non-Epic Event destiny.");
        setLegacyVersion(new LegacyVersion("1240_4", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
