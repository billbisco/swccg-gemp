package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set6.dark.Card6_159;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Trap Door. */
public class Card15_296 extends Card6_159 {
    public Card15_296() {
        setLore("");
        setCredits(3);
        setGameText("If you have no characters in the rancor pit at the end of your deploy phase, target a character (even a captive) at audience chamber. Draw destiny. If destiny +2 > ability, target relocated to Rancor Pit, and, if captive, released to Light Side of Rancor Pit.");
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
