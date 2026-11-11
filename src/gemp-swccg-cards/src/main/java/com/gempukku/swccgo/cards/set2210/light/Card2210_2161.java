package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_161;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Return Of A Jedi
 *
 * PC-E2 version of 1210_161: the PC card image (returnofajedi.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2161 extends Card1210_161 {
    public Card2210_2161() {
        super();
        setGameText("Use 1 Force to deploy (or retrieve) Obi-Wan's Cape, Obi-Wan's Lightsaber, or Obi-Wan from Reserve Deck; reshuffle. OR Unless Inner Strength on table, use 1 Force to cancel a just drawn weapon destiny targeting Obi-Wan (or a just drawn duel or lightsaber combat destiny if Obi-Wan is participating).");
        setLegacyVersion(new LegacyVersion("1210_161", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
