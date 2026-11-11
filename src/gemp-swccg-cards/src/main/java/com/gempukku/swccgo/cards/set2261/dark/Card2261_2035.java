package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_035;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: He's No Jedi
 * PC-E2 version of 1261_35: the Players Committee card image hesnojedi.gif (folder Virtual6-Dark, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2035 extends Card1261_035 {
    public Card2261_2035() {
        super();
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If This Deal Is Getting Worse All The Time on table, for remainder of game lightsabers may not add to Force drains. Cloud City Occupation is canceled.");
        setLegacyVersion(new LegacyVersion("1261_35", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
