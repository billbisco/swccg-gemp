package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Title: Captain Piett (V)
 * PC-E2 version of 1220_6: the Players Committee card image captainpiett.gif (folder Virtual2-Dark, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2006 extends Card1220_006 {
    public Card2220_2006() {
        super();
        setGameText("Adds 3 to power of anything he pilots. Twice per game during opponent's turn, may place three cards from hand on your Force Pile to draw top two cards from Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1220_6", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
