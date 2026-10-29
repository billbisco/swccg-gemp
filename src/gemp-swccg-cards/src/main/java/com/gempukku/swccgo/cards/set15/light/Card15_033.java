package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set14.light.Card14_032;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Gungan Energy Shield. */
public class Card15_033 extends Card14_032 {
    public Card15_033() {
        setLore("");
        setCredits(3);
        setGameText("Deploy on an exterior site. While your Fambaa here: your characters here may not be targeted by weapons, opponent's battle destiny draws here are -1, and characters must use +1 Force when moving to or from here using their landspeed.");
        setAlternateDestiny(3);
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
        return 3f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
