package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_047;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Scout Recon (V)
 * PC-E1 version of 1230_47: the PC website card list text (by 2012-07-21): the +3 to total battle destiny only during a
 * battle at a site where you have an Imperial and opponent has an alien. The Virtual Cards Master List (2013-04-27,
 * PC-E3) repeats the 2009 sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1047 extends Card1230_047 {
    public Card2230_1047() {
        super();
        setGameText("Find one of your missing characters. OR if your scout is in a battle, activate X Force, where X = the number of scouts you have in that battle. OR add 3 to your total battle destiny during a battle at a site where you have an Imperial and opponent has an alien.");
        setLegacyVersion(new LegacyVersion("1230_47", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected Filter battleDestinyBonusLocation() {
        return Filters.site;
    }
}
