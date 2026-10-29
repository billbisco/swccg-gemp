package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set1.dark.Card1_271;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of The Circle Is Now Complete. */
public class Card15_289 extends Card1_271 {
    public Card15_289() {
        setLore("");
        setCredits(3);
        setGameText("If Vader and Obi-Wan are present at same site, use 1 Force to initiate a duel between them. Compare their power, and add 2 if that character is armed with a lightsaber. Loser (lowest total) of duel is placed out of play.");
        setAlternateDestiny(6);
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
        return 6f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNRESTRICTED;
    }
}
