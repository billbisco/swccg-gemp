package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set3.dark.Card3_114;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Weapon Malfunction. */
public class Card15_214 extends Card3_114 {
    public Card15_214() {
        setLore("");
        setCredits(1);
        setGameText("Deploy on any docking bay. Target a weapon deployed on a starfighter or vehicle. That weapon cannot be fired. Utinni Effect canceled when reached by target.");
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
