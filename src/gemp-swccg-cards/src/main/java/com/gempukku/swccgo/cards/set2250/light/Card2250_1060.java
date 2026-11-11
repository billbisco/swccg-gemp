package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_060;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: An Unusual Amount Of Fear (V)
 * PC-E1 version of 1250_60: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1060 extends Card1250_060 {
    public Card2250_1060() {
        super();
        setGameText("Deploy on Jedi Council Chamber or Yoda's Hut. Once per turn, may use 1 Force to take a card with 'A Jedi's' in title (except A Jedi's Resilience) into hand from Reserve Deck; reshuffle. [Virtual Block 5] Wipe Them Out, All Of Them and opponent's [Sidious] Effects here are cancelled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_60", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
