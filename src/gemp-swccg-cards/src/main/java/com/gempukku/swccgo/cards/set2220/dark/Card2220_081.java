package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_213;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Vehicle
 * Title: Blizzard 2 (V)
 * Original version of 1220_81 (class 601_213): the Virtual Block 2 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "Walker Barage total" for "Walker Barrage total" (typo as published)), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_081 extends Card601_213 {
    public Card2220_081() {
        super();
        setGameText("May add 1 pilot and 8 passengers. Permanent pilot provides ability of 2. During your control phase, may [upload] one Trample or Walker Barrage. Walker Barrage total is +2. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1220_81", "",
                "Virtual Block 2 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
