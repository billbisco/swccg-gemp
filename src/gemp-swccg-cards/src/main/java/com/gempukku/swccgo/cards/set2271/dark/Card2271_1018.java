package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_018;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: Commence Primary Ignition (V)
 * PC-E1 version of 1271_18: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1018 extends Card1271_018 {
    public Card2271_1018() {
        super();
        setGameText("If Death Star orbits Alderaan (and [Virtual Block 7] Central Core on table), deploy on Death Star and fire Superlaser at Alderaan; it is 'blown away.' Once during your control phase, if Death Star orbiting a planet (except a Rebel Base) without related sites, may fire Superlaser at planet (regardless of Objective restrictions) as follows: Draw destiny. Add 2 if you control that system. If (destiny + Y - Z) > 8, target is 'blown away' and opponent loses 4 Force from Life Force. Y = total Death Star sites where opponent has no presence. Z = total sites at Yavin 4.");
        setLegacyVersion(new LegacyVersion("1271_18", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
