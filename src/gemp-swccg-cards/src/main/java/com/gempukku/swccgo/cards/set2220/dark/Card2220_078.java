package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_212;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Starship
 * Title: Vader's Personal Shuttle (V)
 * Original version of 1220_78 (class 601_212): the Virtual Block 2 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "Whlie" for "While" and "Uncontrolable" for "Uncontrollable" (typos as published)), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_078 extends Card601_212 {
    public Card2220_078() {
        super();
        setGameText("May add 2 pilots and 4 passengers. Vader deploys -3 aboard. While Vader piloting at a battleground, maneuver +2 and you lose no Force to Uncontrollable Fury or You Must Confront Vader.");
        setLegacyVersion(new LegacyVersion("1220_78", "",
                "Virtual Block 2 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
