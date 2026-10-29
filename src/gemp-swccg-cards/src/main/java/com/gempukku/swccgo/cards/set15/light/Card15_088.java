package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set8.light.Card8_055;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of I Have A Really Bad Feeling About This. */
public class Card15_088 extends Card8_055 {
    public Card15_088() {
        setLore("");
        setCredits(3);
        setGameText("If opponent just initiated a battle where opponent has more than double your power, target your highest-ability character in that battle. Draw destiny. If destiny < target's ability, deploy one character there from hand (for free).");
        setAlternateDestiny(5);
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
        return 5f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
