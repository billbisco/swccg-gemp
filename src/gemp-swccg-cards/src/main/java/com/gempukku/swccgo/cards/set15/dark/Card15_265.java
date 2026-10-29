package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set6.dark.Card6_157;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of None Shall Pass. */
public class Card15_265 extends Card6_157 {
    public Card15_265() {
        setLore("");
        setCredits(3);
        setGameText("If opponent just deployed a Rebel to a Jabba's Palace site, (and you have no Imperials at a Jabba's Palace site), return Rebel to opponent's hand. Any Force used to deploy that Rebel remains used, and Rebel may not be deployed for the remainder of the turn.");
        setAlternateDestiny(1);
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
        return 1f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNRESTRICTED;
    }
}
