package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_018;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: An Enemy Of The Republic
 * PC-E1 version of 1250_18: the PC website card list text (by 2012-07-21): opponent relocates this card to their
 * participating non-droid character, and "may ▲ any one card". The Virtual Cards Master List (2013-04-27, PC-E3) repeats
 * the 2009 sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1018 extends Card1250_018 {
    public Card2250_1018() {
        super();
        setGameText("Deploy on Sidious. While at Senate, if battle was just initiated anywhere, opponent must relocate this card to their participating non-droid character; during battle here, your destiny draws may not be canceled. If character about to be lost, relocate to Sidious; may ▲ any one card.");
        setLegacyVersion(new LegacyVersion("1250_18", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected Filter relocationTargetFilter() {
        return Filters.and(Filters.character, Filters.not(Filters.droid));
    }
}
