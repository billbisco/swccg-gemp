package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Ur'Ru'r
 *
 * PC-E1 version of 1210_26: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1026 extends Card1210_026 {
    public Card2210_1026() {
        super();
        setGameText("Deploys only on Tatooine. During your deploy phase, if with URoRRuR'R'R, may deploy a weapon with 'Tusken' in lore here from Reserve Deck; reshuffle; your weapon destiny draws for such weapons are +2. During your deploy phase, may move Ur'Ru'r as a regular move.");
        setLegacyVersion(new LegacyVersion("1210_26", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
