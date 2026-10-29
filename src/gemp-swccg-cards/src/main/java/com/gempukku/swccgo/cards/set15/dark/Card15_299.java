package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set4.dark.Card4_151;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Uncertain Is The Future. */
public class Card15_299 extends Card4_151 {
    public Card15_299() {
        setLore("");
        setCredits(1);
        setGameText("Use 3 Force. Each player counts cards in hand, then places entire hand and Used Pile onto Reserve Deck; reshuffle. Each player then draws from Reserve Deck the counted number of cards to create a new hand.");
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
