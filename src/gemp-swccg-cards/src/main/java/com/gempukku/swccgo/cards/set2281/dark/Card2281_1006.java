package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: R'tic H'weei
 * PC-E1 version of 1281_6: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1006 extends Card1281_006 {
    public Card2281_1006() {
        super();
        setGameText("Once per game, may take one Jawa Blaster or a card with 'sandcrawler' in title or gametext into hand from Reserve Deck; reshuffle. If present at a Tatooine battleground, once per turn, if there is more than 1 card in opponent's Force Pile, may use 1 Force in opponent's Force Pile.");
        setLegacyVersion(new LegacyVersion("1281_6", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
