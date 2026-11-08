package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Subtype: Used
 * Title: One Beautiful Thing
 * PC-E1 version of 1261_46: the Virtual Cards Master List text (2013-04-27), Juno Eclipse instead of Black Leader;
 * plays as the original (2261_46): a Used Interrupt counting your cards with ability at the battle location.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1046 extends Card2261_046 {
    public Card2261_1046() {
        setGameText("Lose 1 Force to ▲ (or retrieve) Juno Eclipse. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0).");
        setLegacyVersion(new LegacyVersion("1261_46", "PC-E1",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), null));
    }
}
