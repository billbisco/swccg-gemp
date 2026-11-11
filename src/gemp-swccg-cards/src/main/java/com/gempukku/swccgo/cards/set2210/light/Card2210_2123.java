package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_123;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Hidden Fortress
 *
 * PC-E2 version of 1210_123: the PC card image (hiddenfortress.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2123 extends Card1210_123 {
    public Card2210_2123() {
        super();
        setGameText("Deploy on table (only at start of game) with up to three systems from outside your deck face-down under here. If non-[Virtual Block 4] Hidden Base on table: once per game, may deploy a system from here and/or exchange Hidden Base indicator with a system (from parsec 1 to 8) under here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_123", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
