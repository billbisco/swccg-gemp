package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set11.dark.Card11_085;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Podracer Collision. */
public class Card15_271 extends Card11_085 {
    public Card15_271() {
        setLore("");
        setCredits(3);
        setGameText("USED: If you just verified opponent's Reserve Deck, search that Reserve Deck and place one Interrupt found there out of play. LOST: Lose 1 Force to remove a race destiny (random selection) from any Podracer and place it in owner's hand.");
        setAlternateDestiny(5);
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
        return 5f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
