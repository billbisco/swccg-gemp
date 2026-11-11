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
 * PC-E1 version of 1210_161: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1161 extends Card1210_161 {
    public Card2210_1161() {
        super();
        setGameText("Use 1 Force to deploy (or retrieve) Obi-Wan, Obi-Wan's Cape, or Obi-Wan's Lightsaber from Reserve Deck; reshuffle. OR Unless Inner Strength on table, use 1 Force to cancel a just drawn weapon destiny targeting Obi-Wan (or a just drawn duel or lightsaber combat destiny if Obi-Wan is participating).");
        setLegacyVersion(new LegacyVersion("1210_161", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
