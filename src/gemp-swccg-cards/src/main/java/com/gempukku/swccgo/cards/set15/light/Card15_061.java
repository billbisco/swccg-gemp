package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set7.light.Card7_082;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of All Wings Report In. */
public class Card15_061 extends Card7_082 {
    public Card15_061() {
        setLore("");
        setCredits(3);
        setGameText("Once per game, target one non-unique starfighter on table. Draw destiny. If destiny < X, retrieve X Force; X = number of copies of that card you have on table. (Immune to Sense.) OR Take one non-unique starfighter into hand from Reserve Deck; reshuffle.");
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
