package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_250;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Overseer
 * PC-E2 version of 1261_106: the Players Committee card image overseer.gif (folder Virtual6-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2106 extends Card601_250 {
    public Card2261_2106() {
        super();
        setGameText("May add Harc as a pilot. During battle, adds one destiny to total power. If a Dark Jedi or Sith was just deployed to a related site, may ▼ a related location. Immune to attrition < 4 (< 6 at Bespin).");
        setLegacyVersion(new LegacyVersion("1261_106", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
