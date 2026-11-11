package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_047;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Title: Scout Recon (V)
 * PC-E2 version of 1230_47: the Players Committee card image scoutrecon.gif (folder Virtual3-Dark, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2047 extends Card1230_047 {
    public Card2230_2047() {
        super();
        setGameText("Find one of your missing characters. OR During battle, activate 1 Force for each of your scouts participating. OR During battle, if your Imperial with opponent's alien, your total battle destiny is +3.");
        setLegacyVersion(new LegacyVersion("1230_47", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
