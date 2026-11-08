package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_015;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Well Guarded (V)
 * Original version of 1281_15 (Virtual Block 8.1 release sheet): "Your non-unique aliens" are immune (the PC website card list, 2012-07-21, PC-E1, says "Non-unique aliens"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_015 extends Card1281_015 {
    public Card2281_015() {
        super();
        setGameText("Deploy on table. Your non-unique aliens are immune to Grimtaash (while you have < 13 cards in hand), The Camp, and deployment costs on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_15", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }

    /** The release sheet says "Your non-unique aliens". */
    @Override
    protected Filter immuneAliens(String playerId) {
        return Filters.and(Filters.owner(playerId), Filters.non_unique, Filters.alien);
    }
}
