package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_098;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Vanden Willard
 *
 * PC-E1 version of 1210_98: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1098 extends Card1210_098 {
    public Card2210_1098() {
        super();
        setGameText("Leia is defense value and forfeit +3. Your senators at battleground sites and characters at Yavin 4 locations are forfeit +2. Once per game, may take a non-[Permanent Weapon] Leia into hand from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1210_98", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
