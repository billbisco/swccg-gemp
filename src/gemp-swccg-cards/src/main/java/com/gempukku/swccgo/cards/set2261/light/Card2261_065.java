package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_065;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: BoShek, Brash Smuggler
 * Original version of 1261_65: the Virtual Block 6.1 release sheet text, "adds 1 to attrition against opponent" with
 * no "here" (the Virtual Cards Master List, PC-E2 and the current card, adds "here"). Read literally: while BoShek
 * pilots a freighter at a battleground system, attrition against opponent is +1 in any battle. Gameplay test:
 * Card_1261_065_Tests.OriginalVersionAttritionAgainstOpponentIsNotLimitedToHere.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_065 extends Card1261_065 {
    public Card2261_065() {
        setGameText("[Pilot] 3. While piloting a freighter at a battleground system, adds 1 to attrition against opponent and their starships here may not 'cloak.' Opponent loses no more than 3 Force to your Force drains at same system.");
        setLegacyVersion(new LegacyVersion("1261_65", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #63", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected Filter attritionLocations(PhysicalCard self) {
        return Filters.location;
    }
}
