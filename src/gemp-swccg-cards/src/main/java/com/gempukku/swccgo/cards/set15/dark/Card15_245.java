package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set11.dark.Card11_081;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Hit Racer. */
public class Card15_245 extends Card11_081 {
    public Card15_245() {
        setLore("");
        setCredits(3);
        setGameText("During your control phase, use 2 Force to target an opponent's Podracer with a race total within 4 of one of your race totals. Draw destiny. If destiny > target's destiny number, opponent may not draw race destiny this turn and target is 'damaged.'");
        setAlternateDestiny(4);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
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
