package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set2.light.Card2_054;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Out Of Commission. */
public class Card15_119 extends Card2_054 {
    public Card15_119() {
        setLore("");
        setCredits(2);
        setGameText("During your control phase, use 2 Force to release a starship held by any Tractor Beam. (Not effective on Death Star Tractor Beam if Central Core in play.) OR Randomly select one card from opponent's Lost Pile or Blaster Rack and place out of play.");
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
