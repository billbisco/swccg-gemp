package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set5.light.Card5_038;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Clash Of Sabers. */
public class Card15_070 extends Card5_038 {
    public Card15_070() {
        setLore("");
        setCredits(3);
        setGameText("Use 2 Force to target a character present with your warrior with a lightsaber. Target cannot move or battle until end of your next turn. OR Cancel Presence Of The Force. OR Use 1 Force to take Uncontrollable Fury into hand from Reserve Deck; reshuffle.");
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
