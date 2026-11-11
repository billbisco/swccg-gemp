package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_055;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Kessel: Spice Mines - Extraction Facility PC-E1: Episode I icon covered (as the current card).
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2240_1055 extends Card1240_055 {
    public Card2240_1055() {
        super();
        setLegacyVersion(new LegacyVersion("1240_55", "PC-E1",
                "PC forum p637956 (2011-07-25): slip changed to cover the Episode I icon", LocalDate.of(2011, 7, 25), LocalDate.of(2013, 2, 25)));
    }
}
