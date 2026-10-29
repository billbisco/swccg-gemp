package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set11.light.Card11_037;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Losing Track. */
public class Card15_098 extends Card11_037 {
    public Card15_098() {
        setLore("");
        setCredits(3);
        setGameText("USED: During battle, place top card of opponent's Lost Pile on top of their Reserve Deck. LOST: Use 1 Force to 'repair' your Podracer. OR Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target is 'damaged.'");
        setAlternateDestiny(6);
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
        return 6f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
