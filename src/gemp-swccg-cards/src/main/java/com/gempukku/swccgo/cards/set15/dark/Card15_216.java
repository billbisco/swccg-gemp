package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set8.dark.Card8_133;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Well-Earned Command. */
public class Card15_216 extends Card8_133 {
    public Card15_216() {
        setLore("");
        setCredits(2);
        setGameText("Deploy on your general or commander. When battling, adds 1 to your total battle destiny (or 2 if Igar). Once during your control phase, may take Imperial Propaganda into hand from Reserve Deck; reshuffle. Your Force drains are +1 at holosites.");
        setAlternateDestiny(4);
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
        return 4f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
