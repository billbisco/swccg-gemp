package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_152;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Prophetess (V)
 *
 * PC-E2 version of 1210_17: the PC card image (prophetess.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2017 extends Card601_152 {
    public Card2210_2017() {
        super();
        setGameText("Deploys -1 and forfeit +3 at a site. Once per turn, may peek at top card of opponent's Reserve Deck or Used Pile; may then shuffle that Reserve Deck or Used Pile. Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1210_17", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
