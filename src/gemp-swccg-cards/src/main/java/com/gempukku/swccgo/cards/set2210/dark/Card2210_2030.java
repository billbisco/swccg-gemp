package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_030;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: A Disturbance In The Force
 *
 * PC-E2 version of 1210_30: the PC card image (adisturbanceintheforce.gif), dated by its server upload
 * (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of
 * 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its
 * upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2030 extends Card1210_030 {
    public Card2210_2030() {
        super();
        setGameText("Deploy on table. Whenever you verify opponent's Reserve Deck, may place a card there with 'podracer' in game text out of play or retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1210_30", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
