package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set13.dark.Card13_093;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Through The Corridor. */
public class Card15_293 extends Card13_093 {
    public Card15_293() {
        setLore("");
        setCredits(2);
        setGameText("If opponent just initiated battle against Maul where opponent has two Jedi, cancel that battle and you may relocate one of those Jedi (your choice) to any adjacent site. That Jedi may not move for remainder of turn. (Immune to Sense.)");
        setAlternateDestiny(3);
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
        return 3f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
