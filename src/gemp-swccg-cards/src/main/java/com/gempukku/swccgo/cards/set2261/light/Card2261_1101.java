package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_101;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Solo Han (V)
 * PC-E1 version of 1261_101: the PC website card list text (by 2012-07-21): non-[Permanent Weapon] Han (except Falcon), and Han is power +1 for each character and vehicle present. The Virtual Cards Master List
 * (2013-04-27, PC-E3) repeats the release-sheet words and is the current card; the PC card image (PC-E2, 2261_2101) came between. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1101 extends Card1261_101 {
    public Card2261_1101() {
        super();
        setGameText("Use 1 Force to deploy (or retrieve) non-[Permanent Weapon] Han (except Falcon) from Reserve Deck; reshuffle. OR If battle just initiated, and Han defending alone at a site, he is power +1 for each character and vehicle present, his forfeit cannot be reduced, he adds one destiny to power, and he draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_101", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected Filter searchFilter() {
        return Filters.and(Filters.Han, Filters.character, Filters.not(Icon.PERMANENT_WEAPON));
    }

    @Override
    protected boolean bonusIsPowerNotDestiny() {
        return true;
    }
}
