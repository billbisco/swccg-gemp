package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_227;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Title: Leia (V)
 * PC-E2 version of 1210_103: the Players Committee card image leia.gif (folder Virtual1-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2103 extends Card601_227 {
    public Card2210_2103() {
        super();
        setGameText("While at opponent's Galactic Senate, Leia's game text may not be canceled and may lose 1 Force to place Leia and any character present out of play. During battle, may cancel game text of a leader of ability < 5 with Leia for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1210_103", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
