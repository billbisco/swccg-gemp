package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set12.light.Card12_072;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of We Wish To Board At Once. */
public class Card15_148 extends Card12_072 {
    public Card15_148() {
        setLore("");
        setCredits(3);
        setGameText("Use 3 Force to take one Effect of any kind into hand from your Reserve Deck; reshuffle. OR Take Radiant VII, or one Interrupt with the word 'Podracer(s)' in its game text, into hand from Reserve Deck; reshuffle.");
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
