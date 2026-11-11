package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Jedi Master
 * Title: Mace Windu (V)
 *
 * PC-E2 version of 1250_45ai: the PC card image (macewinduai.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2045ai extends Card2250_2045 {
    public Card2250_2045ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_45ai", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
