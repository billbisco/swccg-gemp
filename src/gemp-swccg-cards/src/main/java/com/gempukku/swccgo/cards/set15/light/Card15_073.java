package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set2.light.Card2_047;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Corellian Slip. */
public class Card15_073 extends Card2_047 {
    public Card15_073() {
        setLore("");
        setCredits(1);
        setGameText("If opponent just initiated a Tallon Roll, add your maneuver + ability of one pilot on your targeted starfighter to your total. OR Decrease the maneuver of an opponent's starfighter by 1 for remainder of this turn.");
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
        return Uniqueness.UNRESTRICTED;
    }
}
