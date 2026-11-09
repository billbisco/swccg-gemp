package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_059;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: 
 * Title: A Jedi's Plans (V)
 * Original version of 1250_59: the Virtual Block 5 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "cancelled" for "canceled" and "(except sector)" for "(except a sector)"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_059 extends Card1250_059 {
    public Card2250_059() {
        super();
        setGameText("Deploy on a site. Nightfall is canceled. While you have 8 or fewer cards in hand, non-unique characters (except clones), vehicles, starships, weapons, and devices are immune to Monnok. Once per turn, may use 2 Force to deploy Jedi Archives or a battleground planet location (except a sector) not on table from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_59", "",
                "Virtual Block 5 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
