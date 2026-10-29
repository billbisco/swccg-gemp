package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set5.dark.Card5_119;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of I Had No Choice. */
public class Card15_183 extends Card5_119 {
    public Card15_183() {
        setLore("");
        setCredits(3);
        setGameText("Deploy on an opponent's gambler. Cancels gambler's game text. If a battle was just initiated, you may use X Force to exclude gambler from that battle, where X = gambler's ability.");
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
