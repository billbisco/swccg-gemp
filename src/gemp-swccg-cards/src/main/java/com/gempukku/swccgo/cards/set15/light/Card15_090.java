package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set12.light.Card12_059;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Inconsequential Barriers. */
public class Card15_090 extends Card12_059 {
    public Card15_090() {
        setLore("");
        setCredits(1);
        setGameText("Cancel Imperial Barrier, None Shall Pass, or Set For Stun. OR Peek at the top card of your Force Pile; either return it, or place it under your Force Pile or Reserve Deck.");
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
