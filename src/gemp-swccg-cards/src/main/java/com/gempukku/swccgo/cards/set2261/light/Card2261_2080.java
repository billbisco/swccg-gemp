package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_080;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Jabba, This Is Your Last Chance!
 * PC-E2 version of 1261_80: the Players Committee card image jabbathisisyourlastchance.gif (folder Virtual6-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2080 extends Card1261_080 {
    public Card2261_2080() {
        super();
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains. Cloud City Celebration is canceled.");
        setLegacyVersion(new LegacyVersion("1261_80", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
