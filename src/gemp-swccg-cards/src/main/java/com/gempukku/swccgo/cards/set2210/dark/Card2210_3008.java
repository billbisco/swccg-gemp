package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Death Star Gunner
 *
 * PC-E3 version of 1210_8: the PC card image (deathstargunner.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the PC-E1 errata wording of 2010-06-17. The Master List of 2013-04-27
 * (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date;
 * batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_3008 extends Card1210_008 {
    public Card2210_3008() {
        super();
        setGameText("While on Death Star: cancels Force drain bonuses from Death Star sites' game text, your [A New Hope] Epic Event total is cumulatively +2, and during your deploy phase, may place this character in Used Pile to ▲ Superlaser.");
        setLegacyVersion(new LegacyVersion("1210_8", "PC-E3",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
