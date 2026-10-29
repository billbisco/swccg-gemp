package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set4.light.Card4_037;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Scramble. */
public class Card15_049 extends Card4_037 {
    public Card15_049() {
        setLore("");
        setCredits(4);
        setGameText("Deploy on opponent's side of table. Opponent's pilots (except Vader) present at sites may not use ability to draw battle destiny. Effect canceled when opponent has no pilots at sites.");
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
