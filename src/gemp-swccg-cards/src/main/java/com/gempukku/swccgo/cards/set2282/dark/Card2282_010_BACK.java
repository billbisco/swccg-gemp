package com.gempukku.swccgo.cards.set2282.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_029_BACK;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Objective
 * Title: Wookiee Slaving Operation / Indentured To The Empire (back)
 *
 * Original version of 1282_10_BACK (current class 601_29_BACK): the Virtual Block 8.2 release sheet (official text as of
 * 2012-09-14). The Competitive Balance Team errata of 2014-07-23 (PC forum "Current Rulings/Errata", p894811) changed
 * the 7 side (Indentured To The Empire) ("two Kashyyyk battlegrounds" -> "two Kashyyyk locations"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_010_BACK extends Card601_029_BACK {
    public Card2282_010_BACK() {
        super();
        setGameText("While this side up, whenever you 'enslave' a character, opponent must choose to use 2 Force or lose 1 Force. Once per turn, during battle may add or subtract up to X from your just drawn destiny, where X = the number of Kashyyyk locations you control with a slaver. Once per turn, if you just retrieved Force during battle, may take a slaver, starship, or vehicle retrieved into hand.\n" +
                "Flip this card if opponent controls two Kashyyyk battlegrounds.");
        setLegacyVersion(new LegacyVersion("1282_10_BACK", "",
                "Virtual Block 8.2 release sheet (2012-09-14)",
                LocalDate.of(2012, 9, 14), LocalDate.of(2014, 7, 23)));
    }

    @Override
    protected Filter opponentFlipLocations() {
        return Filters.and(Filters.Kashyyyk_location, Filters.battleground);
    }
}
