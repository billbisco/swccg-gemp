package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set5.light.Card5_069;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Smoke Screen. */
public class Card15_130 extends Card5_069 {
    public Card15_130() {
        setLore("");
        setCredits(4);
        setGameText("During a battle at a site, if you are about to draw a card for battle destiny, you may instead use the ability number of one of your characters involved in the battle.");
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
