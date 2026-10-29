package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set7.dark.Card7_255;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Jabba's Through With You. */
public class Card15_258 extends Card7_255 {
    public Card15_258() {
        setLore("");
        setCredits(2);
        setGameText("If your bounty hunter just fired a character weapon during a battle, add one battle destiny. OR During your deploy phase, deploy on your bounty hunter one character weapon from Reserve Deck; reshuffle.");
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
