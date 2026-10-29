package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set11.light.Card11_016;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Brisky Morning Munchen. */
public class Card15_026 extends Card11_016 {
    public Card15_026() {
        setLore("");
        setCredits(3);
        setGameText("Deploy on table. Once per game, may deploy Jar Jar from Reserve Deck; reshuffle. If you just lost Jar Jar from a site, may place him here. At any time, may use 3 Force to take Jar Jar from here into hand.");
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
