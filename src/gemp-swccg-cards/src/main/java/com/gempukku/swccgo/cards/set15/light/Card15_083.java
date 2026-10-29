package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set5.light.Card5_049;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Glancing Blow. */
public class Card15_083 extends Card5_049 {
    public Card15_083() {
        setLore("");
        setCredits(3);
        setGameText("If you have a character with a lightsaber present in a battle with Vader, lose 1 Force to reduce Vader's power to zero. OR If you have a character with a lightsaber in a duel, cancel one opponent's destiny draw immediately after it is revealed.");
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
