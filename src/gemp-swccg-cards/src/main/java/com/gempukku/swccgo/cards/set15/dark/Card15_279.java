package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set7.dark.Card7_262;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Short Range Fighters. */
public class Card15_279 extends Card7_262 {
    public Card15_279() {
        setLore("");
        setCredits(3);
        setGameText("Once per game, target one non-unique starfighter on table. Draw destiny. If destiny < X, retrieve X Force, where X = number of copies of that card you have on table. (Immune to Sense.) OR Take one non-unique starfighter into hand from Reserve Deck; reshuffle.");
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
