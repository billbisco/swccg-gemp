package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Forced Servitude
 *
 * PC-E2 version of 1210_36: the PC card image (forcedservitude.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2036 extends Card1210_036 {
    public Card2210_2036() {
        super();
        setGameText("Deploy on table. Your troopers are Heroic. Opponent's total weapon destiny targeting your Heroic card is +2. When opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_36", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
