package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_051;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Let The Wookiee Win (V)
 * PC-E2 version of 1261_97: the Players Committee card image letthewookieewin.gif (folder Virtual6-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2097 extends Card601_051 {
    public Card2261_2097() {
        super();
        setGameText("USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order). STARTING: If your starting location was a non-[Special Edition] location, ▼ an [Immune to Alter] Effect and any number of <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1261_97", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
