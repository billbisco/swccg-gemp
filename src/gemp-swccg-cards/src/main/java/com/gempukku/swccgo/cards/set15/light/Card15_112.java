package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set14.light.Card14_043;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of No Giben Up, General Jar Jar!. */
public class Card15_112 extends Card14_043 {
    public Card15_112() {
        setLore("");
        setCredits(3);
        setGameText("If you just lost a Gungan from table, place him in your hand. OR If Jar Jar and Tarpals are in a battle together, add two battle destiny.");
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
