package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: Nute Gunray's Bounty
 * Original version of 1250_19: the Virtual Block 5 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "sieze" for "seize" (typo as published)), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_019 extends Card1250_019 {
    public Card2250_019() {
        super();
        setGameText("Deploy on Amidala or opponent's senator. During your move phase, unless a Jedi here, your alien bounty hunter present may capture and seize that character. During your control phase, if that character is escorted by an alien, opponent loses 1 Force.");
        setLegacyVersion(new LegacyVersion("1250_19", "",
                "Virtual Block 5 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
