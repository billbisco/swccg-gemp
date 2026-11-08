package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: One Beautiful Thing
 * PC-E1 version of 1261_46: the PC website card list text (by 2012-07-21), naming Black Leader (the Juno persona)
 * and counting "only two participating cards with ability". A Used Interrupt as printed. (This id held the Master
 * List text, labelled PC-E1, until the current card took that text, PC-E2.) Gameplay test:
 * Card_601_092_Tests.BattleClauseCountsYourNonParticipatingCardWithAbilityThere. Image: the current card image.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1046 extends Card601_092 {
    public Card2261_1046() {
        setGameText("Lose 1 Force to take Black Leader into hand from Reserve Deck (or retrieve); reshuffle. OR During battle where you have no Dark Jedi or droids and only two participating cards with ability (a male and a female), add one destiny to total power (and attrition against you = 0).");
        setLegacyVersion(new LegacyVersion("1261_46", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected String junoName() {
        return "Black Leader";
    }

    @Override
    protected boolean countsOnlyParticipatingCards() {
        return true;
    }
}
