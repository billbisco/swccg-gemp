package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_024;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Block 8
 * Type: Starship
 * Subtype: Starfighter
 * Title: Slave I, Symbol Of Fear
 * Original version of 1281_23 (Virtual Block 8.1 release sheet): "[S] a Fett" and "Any Fett deploys -2 aboard" (the PC website card list, 2012-07-21, PC-E1, says "[Virtual Block 8] Fett" and "Your [Virtual Block 8] Fetts"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_023 extends Card601_024 {
    public Card2281_023() {
        super();
        setGameText("May reveal from hand to ▲ a [Virtual Block 8] Fett and deploy both simultaneously. May add 2 pilots and 2 passengers. Any [Virtual Block 8] Fett deploys -2 aboard. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1281_23", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }

    /** The release sheet says "▲ a [Virtual Block 8] Fett". */
    @Override
    protected Filter fettToTake() {
        return Filters.and(Filters.Fett, Icon.LEGACY_BLOCK_8);
    }

    /** The release sheet says "Any [Virtual Block 8] Fett deploys -2 aboard" (either player's). */
    @Override
    protected Filter fettsDeployMinus2(PhysicalCard self) {
        return Filters.and(Filters.Fett, Icon.LEGACY_BLOCK_8);
    }
}
