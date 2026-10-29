package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set2.dark.Card2_139;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Sniper. */
public class Card15_281 extends Card2_139 {
    public Card15_281() {
        setLore("");
        setCredits(3);
        setGameText("During your control phase, fire one of your weapons. If URoRRuR'R'R firing, may add 2 to each weapon destiny draw. (A seeker may be targeted by a character weapon using a defense value of 4.) Any 'hit' targets are immediately lost.");
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
