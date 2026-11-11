package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: Nute Gunray's Bounty
 *
 * PC-E1 version of 1250_19: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2250_1019 extends Card1250_019 {
    public Card2250_1019() {
        super();
        setGameText("Deploy on Amidala or opponent's senator. During your move phase, unless a Jedi here, your alien bounty hunter present may capture and sieze that character. During your control phase, if that character is escorted by an alien, opponent loses 1 Force.");
        setLegacyVersion(new LegacyVersion("1250_19", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
