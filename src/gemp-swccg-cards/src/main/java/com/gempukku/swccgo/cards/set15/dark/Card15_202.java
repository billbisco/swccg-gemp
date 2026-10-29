package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set8.dark.Card8_130;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Relentless Tracking. */
public class Card15_202 extends Card8_130 {
    public Card15_202() {
        setLore("");
        setCredits(1);
        setGameText("Deploy on opponent's non-droid character at same or adjacent site as your scout. During each of your control phases, if your scout is at this site (and not Undercover), opponent loses 2 Force (or 3 Force if that scout is also a biker scout).");
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
