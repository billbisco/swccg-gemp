package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set2.dark.Card2_142;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of We Have A Prisoner. */
public class Card15_303 extends Card2_142 {
    public Card15_303() {
        setLore("");
        setCredits(1);
        setGameText("Use 1 Force if opponent just lost or forfeited a character from battle. Character is captured instead. OR Use X Force to capture all characters aboard a captured starship, where X = twice the number of characters.");
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
        return Uniqueness.UNRESTRICTED;
    }
}
