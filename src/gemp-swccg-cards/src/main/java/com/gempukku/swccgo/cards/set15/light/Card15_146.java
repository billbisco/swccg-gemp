package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set4.light.Card4_070;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of We Don't Need Their Scum. */
public class Card15_146 extends Card4_070 {
    public Card15_146() {
        setLore("");
        setCredits(2);
        setGameText("USED: Cancel Res Luk Ra'auf. LOST: If two bounty hunters (or an Imperial and a bounty hunter) are in a battle together, use 2 Force to draw two destiny. Subtract total from opponent's attrition and total power (cannot fall below zero).");
        setAlternateDestiny(2);
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
        return 2f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNRESTRICTED;
    }
}
