package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set2.light.Card2_057;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Sorry About The Mess. */
public class Card15_132 extends Card2_057 {
    public Card15_132() {
        setLore("");
        setCredits(3);
        setGameText("During your control phase, fire one of your weapons. If Han firing, may add 1 to each weapon destiny draw. (A seeker may be targeted by a character weapon using defense value 4.) Any 'hit' targets are immediately lost.");
        setAlternateDestiny(4);
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
        return 4f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
