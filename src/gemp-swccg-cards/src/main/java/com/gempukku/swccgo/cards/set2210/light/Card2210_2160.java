package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_160;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Rebel Reinforcements
 *
 * PC-E2 version of 1210_160: the PC card image (rebelreinforcements.gif), dated by its server upload (2013-02-25,
 * a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2160 extends Card1210_160 {
    public Card2210_2160() {
        super();
        setGameText("Once per game, if opponent has more cards with ability on table than you, draw destiny and retrieve up to that number of Rebel characters and/or Rebel starships (may take one retrieved card into hand).");
        setLegacyVersion(new LegacyVersion("1210_160", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
