package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_127;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Title: I Can't Believe He's Gone (V)
 * PC-E2 version of 1210_127: the Players Committee card image icantbelievehesgone.gif (folder Virtual1-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2127 extends Card1210_127 {
    public Card2210_2127() {
        super();
        setGameText("If Obi-Wan is out of play (and not on table), use 1 Force to deploy on table. During battle, may use 1 Force to add 5 to your total power there for remainder of turn. Once per turn, if Luke a captive, may activate 1 Force. (Immune to Control.)");
        setLegacyVersion(new LegacyVersion("1210_127", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
