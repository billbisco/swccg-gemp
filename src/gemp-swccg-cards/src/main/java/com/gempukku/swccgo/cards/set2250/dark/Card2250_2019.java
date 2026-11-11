package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: Nute Gunray's Bounty
 *
 * PC-E2 version of 1250_19: the PC card image (nutegunraysbounty.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2019 extends Card1250_019 {
    public Card2250_2019() {
        super();
        setGameText("Deploy on Amidala or opponent's senator. During your move phase, unless a Jedi here, your alien bounty hunter present may capture and seize that character. During your control phase, if that character is escorted by an alien, opponent loses 1 Force.");
        setLegacyVersion(new LegacyVersion("1250_19", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
