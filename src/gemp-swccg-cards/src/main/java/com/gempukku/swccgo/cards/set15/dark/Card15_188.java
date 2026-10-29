package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set2.dark.Card2_121;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Imperial Justice. */
public class Card15_188 extends Card2_121 {
    public Card15_188() {
        setLore("");
        setCredits(1);
        setGameText("If opponent is generating at least 4 more Force than you, deploy on a location to cancel all Force generation at that location. OR Deploy on a location. It is now immune to Revolution.");
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
        return Uniqueness.UNRESTRICTED;
    }
}
