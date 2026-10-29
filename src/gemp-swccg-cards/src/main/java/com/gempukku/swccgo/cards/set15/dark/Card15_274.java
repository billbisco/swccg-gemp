package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set5.dark.Card5_149;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Projective Telepathy. */
public class Card15_274 extends Card5_149 {
    public Card15_274() {
        setLore("");
        setCredits(2);
        setGameText("Cancel Anger, Fear, Aggression when it is inserted or revealed. OR If your opponent just initiated a battle or Force drain, opponent must choose to use 2 Force, or cancel that battle or Force drain.");
        setAlternateDestiny(3);
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
        return 3f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
