package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set5.dark.Card5_163;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of You Are Beaten. */
public class Card15_307 extends Card5_163 {
    public Card15_307() {
        setLore("");
        setCredits(4);
        setGameText("Use 2 Force to target a character present with your warrior with a lightsaber. Target cannot move or battle until end of your next turn. OR Use 1 Force to take I Am Your Father into hand from Reserve Deck; reshuffle. OR Cancel Uncontrollable Fury.");
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
