package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_075;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Luke Skywalker, Rebel Scout
 * Original version of 1230_75: the Virtual Block 3 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "His weapon destiny draws" for "Luke's weapon destiny draws"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_075 extends Card1230_075 {
    public Card2230_075() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Deploys -2 to a forest. Luke's weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. During opponent's draw phase, if alone on Endor, may retrieve 1 Force. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1230_75", "",
                "Virtual Block 3 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
