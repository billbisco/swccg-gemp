package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_093;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Title: Kebyc (V)
 * PC-E2 version of 1220_93: the Players Committee card image kebyc.gif (folder Virtual2-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2093 extends Card1220_093 {
    public Card2220_2093() {
        super();
        setGameText("Deploys free to Cloud City. You generate +1 Force for every Cloud City site you occupy. Your aliens are deploy -1 here. Opponent's aliens are deploy +1 here.");
        setLegacyVersion(new LegacyVersion("1220_93", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
