package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set7.dark.Card7_220;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Alert My Star Destroyer!. */
public class Card15_170 extends Card7_220 {
    public Card15_170() {
        setLore("");
        setCredits(2);
        setGameText("If your admiral, moff or Dark Jedi just shuttled aboard your Star Destroyer, deploy on that character. Star Destroyer is immune to attrition and Flagship deploys free on that starship. Immediate Effect lost if character not aboard that starship.");
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
        return Uniqueness.UNIQUE;
    }
}
