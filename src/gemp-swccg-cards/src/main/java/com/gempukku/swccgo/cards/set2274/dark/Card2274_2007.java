package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set1274.dark.Card1274_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Title: Occupied Territory
 * PC-E2 version of 1274_7: the Players Committee card image occupiedterritory.gif (folder Virtual7-Dark, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_2007 extends Card1274_007 {
    public Card2274_2007() {
        super();
        setGameText("Deploy on a battleground. If you occupy, during battle here or at a related location, may place this Effect in Used Pile and choose: Draw up to two cards from Reserve Deck OR Prevent opponent from drawing any destinies during the power segment of that battle (except battle destiny).");
        setLegacyVersion(new LegacyVersion("1274_7", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
