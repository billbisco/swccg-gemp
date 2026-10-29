package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set14.dark.Card14_105;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Rolling, Rolling, Rolling. */
public class Card15_277 extends Card14_105 {
    public Card15_277() {
        setLore("");
        setCredits(3);
        setGameText("Take any non-unique destroyer droid into hand from Reserve Deck; reshuffle. OR During your deploy phase, relocate your destroyer droid to an adjacent site.");
        setAlternateDestiny(5);
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
        return 5f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
