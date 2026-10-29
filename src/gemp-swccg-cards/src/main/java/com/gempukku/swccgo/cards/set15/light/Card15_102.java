package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set12.light.Card12_061;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Might Of The Republic. */
public class Card15_102 extends Card12_061 {
    public Card15_102() {
        setLore("");
        setCredits(2);
        setGameText("Once per game, retrieve 1 Force for each of your senators at Galactic Senate. (Immune to Sense.) OR Take one senator (or Coruscant Guard, Sei Taria or Mas Amedda) into hand from Reserve Deck; reshuffle.");
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
