package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set9.dark.Card9_136;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Force Lightning. */
public class Card15_241 extends Card9_136 {
    public Card15_241() {
        setLore("");
        setCredits(2);
        setGameText("Target a character (even a captive) present with Emperor. USED: Target is defense value -4 for rest of turn. LOST: Emperor loses immunity to attrition and is defense value -2 for rest of turn. Draw destiny. Target lost if destiny +1 > defense value.");
        setAlternateDestiny(5);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.REFLECTIONS_GOLD;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNKNOWN;
    }

    @Override
    public Float getDestiny() {
        return 5f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
