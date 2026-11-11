package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Death Star Gunner
 *
 * PC-E2 version of 1210_8: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated from
 * the PC's Holotable card data), which writes the ▲ out as "take a Superlaser into hand from Reserve Deck;
 * reshuffle". Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E3, the current card) prints the PC-E1 errata wording of
 * 2010-06-17 again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2210_2008 extends Card1210_008 {
    public Card2210_2008() {
        super();
        setGameText("While on Death Star: cancels Force drain bonuses from Death Star sites' game text, your [A New Hope] Epic Event total is cumulatively +2, and during your deploy phase, may place this character in Used Pile to take a Superlaser into hand from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1210_8", "PC-E2",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
