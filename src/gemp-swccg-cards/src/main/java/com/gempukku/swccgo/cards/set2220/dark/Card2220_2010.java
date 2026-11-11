package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Title: Dengar (V)
 * PC-E2 version of 1220_10: the Players Committee card image dengar.gif (folder Virtual2-Dark, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2010 extends Card1220_010 {
    public Card2220_2010() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Deploys free aboard Punishing One, and while piloting it, draws one battle destiny if unable to otherwise. At same site, Han's game text is canceled. May not be attacked. Immune to Fallen Portal.");
        setLegacyVersion(new LegacyVersion("1220_10", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
