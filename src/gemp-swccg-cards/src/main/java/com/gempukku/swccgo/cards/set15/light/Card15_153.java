package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set6.light.Card6_080;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of You Will Take Me To Jabba Now. */
public class Card15_153 extends Card6_080 {
    public Card15_153() {
        setLore("");
        setCredits(3);
        setGameText("If Jabba is at a Jabba's Palace site, relocate one of your characters to that location from a related site. OR During your deploy phase, deploy one alien (at normal use of the Force) from Reserve Deck to the Audience Chamber; reshuffle.");
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
