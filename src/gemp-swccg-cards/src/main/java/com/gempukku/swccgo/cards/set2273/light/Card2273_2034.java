package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_034;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Use The Force
 * PC-E2 version of 1273_34: the Players Committee card image usetheforce.gif (folder Virtual7-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2034 extends Card1273_034 {
    public Card2273_2034() {
        super();
        setGameText("Choose up to X of the following in any order, where X = number of cards stacked on Communing: Peek at the top card of any deck or pile; may then shuffle it. OR Draw 2 cards from Reserve Deck. OR Retrieve 1 Force. OR Activate X Force. OR Your next destiny draw this turn is +X.");
        setLegacyVersion(new LegacyVersion("1273_34", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
