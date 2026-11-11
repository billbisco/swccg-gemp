package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_124;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Faithful Service
 *
 * PC-E2 version of 1210_124: the PC card image (faithfulservice.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2124 extends Card1210_124 {
    public Card2210_2124() {
        super();
        setGameText("Deploy on table. If opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). Your troopers are armor = 4. Your unique (•) [Hoth], [Endor], and [Death Star II] non-pilot warriors of destiny > 1 are troopers. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_124", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
