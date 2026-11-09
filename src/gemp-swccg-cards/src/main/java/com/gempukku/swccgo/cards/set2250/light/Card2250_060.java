package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_060;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: 
 * Title: An Unusual Amount Of Fear (V)
 * Original version of 1250_60: the Virtual Block 5 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "cancelled" for "canceled" (the block list and the Virtual master list agree on "cancelled" in every capture)), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_060 extends Card1250_060 {
    public Card2250_060() {
        super();
        setGameText("Deploy on Jedi Council Chamber or Yoda's Hut. Once per turn, may use 1 Force to take a card with 'A Jedi's' in title (except A Jedi's Resilience) into hand from Reserve Deck; reshuffle. [Virtual Block 5] Wipe Them Out, All Of Them and opponent's [Sidious] Effects here are canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_60", "",
                "Virtual Block 5 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
