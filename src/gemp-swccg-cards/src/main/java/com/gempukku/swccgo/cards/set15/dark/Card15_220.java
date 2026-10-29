package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set5.dark.Card5_131;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Atmospheric Assault. */
public class Card15_220 extends Card5_131 {
    public Card15_220() {
        setLore("");
        setCredits(2);
        setGameText("If you just initiated a battle at a Cloud City sector, add one battle destiny. OR During your deploy phase, deploy one TIE Assault Squadron for free (no replacement is necessary).");
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
