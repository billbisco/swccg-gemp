package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set4.light.Card4_025;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Hiding In The Garbage. */
public class Card15_034 extends Card4_025 {
    public Card15_034() {
        setLore("");
        setCredits(3);
        setGameText("Use 2 Force to deploy on table. At any time, you may declare a card type. Use Force one by one, revealing each card used, until a card of that type is revealed (take it into hand) or Force Pile is depleted.");
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
