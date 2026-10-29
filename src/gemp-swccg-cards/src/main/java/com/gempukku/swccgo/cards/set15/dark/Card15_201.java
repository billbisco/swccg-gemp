package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set11.dark.Card11_074;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Quietly Observing. */
public class Card15_201 extends Card11_074 {
    public Card15_201() {
        setLore("");
        setCredits(2);
        setGameText("Deploy on Aurra Sing. While Aurra is alone at a site, your total ability here = 0. During your move phase, may lose Effect to target an opponent's lightsaber present. Draw destiny. If destiny > 3, Aurra may 'steal' that lightsaber.");
        setAlternateDestiny(4);
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
        return 4f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
