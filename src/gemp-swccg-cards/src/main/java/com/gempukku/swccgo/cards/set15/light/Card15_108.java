package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set11.light.Card11_038;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Neck And Neck. */
public class Card15_108 extends Card11_038 {
    public Card15_108() {
        setLore("");
        setCredits(3);
        setGameText("If difference between opponent's and your highest race totals < 4, lose 1 Force. Opponent draws no race destiny next control phase. OR If both players have < 10 Life Force remaining, both players retrieve 4 Force.");
        setAlternateDestiny(4);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
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
        return 4f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
