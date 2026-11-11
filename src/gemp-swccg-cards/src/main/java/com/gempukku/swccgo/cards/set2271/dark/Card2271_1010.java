package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: I Want Every Part Of This Ship Checked
 * PC-E1 version of 1271_10: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1010 extends Card1271_010 {
    public Card2271_1010() {
        super();
        setGameText("Deploy on table. Once per game, if Obi-Wan on table, may deploy Vader from Reserve Deck; reshuffle. May place this Effect in Used Pile to take a [Grabber] into hand from Reserve Deck; reshuffle. If you just attempted to take a card into hand or deploy a card from Reserve Deck, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card instead.");
        setLegacyVersion(new LegacyVersion("1271_10", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
