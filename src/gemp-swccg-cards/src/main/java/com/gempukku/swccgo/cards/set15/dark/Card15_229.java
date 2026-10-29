package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set7.dark.Card7_249;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Counter Surprise Assault. */
public class Card15_229 extends Card7_249 {
    public Card15_229() {
        setLore("");
        setCredits(2);
        setGameText("Cancel Surprise Assault. OR Use 4 Force to cancel opponent's Force drain at a mobile site. Deploy (for free) from Reserve Deck any number of non-unique troopers to that site. (Light Side characters, vehicles and starships here may 'escape' to Used Pile).");
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
