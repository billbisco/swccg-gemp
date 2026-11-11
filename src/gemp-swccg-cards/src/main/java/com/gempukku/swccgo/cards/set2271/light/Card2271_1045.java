package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.set1271.light.Card1271_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: Rebel Aces
 * PC-E1 version of 1271_45: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1045 extends Card1271_045 {
    public Card2271_1045() {
        super();
        setGameText("Deploy on table. Your Sandwhirl is canceled. At battleground systems where you have a Rebel of ability = 2 piloting a starfighter, and you have no Force drain bonus (except from the system card itself), your Force drains are +1. Your starfighters with a pilot character aboard are immune to attrition < 4. Once per turn, may deploy Close Air Support from Reserve Deck; reshuffle. Your Rebel starfighters without astromech capacit (except Falcon or Artoo-Detoo In Red 5) gain 'May add 1 astromech.'");
        setLegacyVersion(new LegacyVersion("1271_45", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
