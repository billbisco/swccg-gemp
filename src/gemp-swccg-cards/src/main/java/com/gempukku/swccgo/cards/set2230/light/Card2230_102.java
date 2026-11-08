package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_102;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Diversionary Tactics
 * Original version of 1230_102: the Virtual Block 3 release sheet text (Dec 2009, slip 103); the PC website card list
 * (by 2012-07-21) has the current wording ("If you have two unique" became "If you have 2 unique"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_102 extends Card1230_102 {
    public Card2230_102() {
        super();
        setGameText("USED: Target one piloted starship at a system. That starship is hyperspeed -1 for remainder of turn. LOST: If you have two unique (•) piloted Rebel (or [Republic]) starfighters in battle at a system, draw destiny. If destiny < 4, exclude one starship from that battle.");
        setLegacyVersion(new LegacyVersion("1230_102", "",
                "Virtual Block 3 release sheet (2009-12-07), card #103; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
