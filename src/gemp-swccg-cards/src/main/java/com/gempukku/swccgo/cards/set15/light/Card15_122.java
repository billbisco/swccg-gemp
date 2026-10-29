package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set2.light.Card2_055;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Quite A Mercenary. */
public class Card15_122 extends Card2_055 {
    public Card15_122() {
        setLore("");
        setCredits(1);
        setGameText("If Elis Helrot just completed a transport, use 2 Force. Elis and all characters transported are lost. (Immune to Sense.) OR Prevent a just-deployed smuggler from moving this turn.");
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
        return Uniqueness.UNRESTRICTED;
    }
}
