package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set8.light.Card8_034;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Aim High. */
public class Card15_022 extends Card8_034 {
    public Card15_022() {
        setLore("");
        setCredits(2);
        setGameText("Deploy on table. When you fire two weapons (except lightsabers) in a battle, your total power is +5. Also, whenever opponent retrieves X cards, opponent must first use X Force or that retrieval is canceled. (Immune to Alter.)");
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
