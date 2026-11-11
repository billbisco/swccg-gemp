package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_180;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Arica (V)
 *
 * PC-E2 version of 1240_1: the PC card image (arica.gif), dated by its server upload (2013-02-25, a bulk upload;
 * the image may be older). It prints the card-list wording ("Immune"). The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2001 extends Card601_180 {
    public Card2240_2001() {
        super();
        setGameText("Power +1 on Tatooine. Once per game, during your move phase, may relocate Arica to Luke's location. During your control phase, if present at a battleground (and Luke is not), may retrieve 1 Force. Immune to Undercover and attrition < 4.");
        setLegacyVersion(new LegacyVersion("1240_1", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
