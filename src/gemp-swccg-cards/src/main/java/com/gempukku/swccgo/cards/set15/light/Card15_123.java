package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set9.light.Card9_054;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Rebel Leadership. */
public class Card15_123 extends Card9_054 {
    public Card15_123() {
        setLore("");
        setCredits(3);
        setGameText("Your admirals and generals are forfeit +1 for remainder of turn. OR If your admiral is in a battle at a system (or your general, except Obi-Wan, is in a battle at a site), either add one battle destiny or prevent opponent from drawing more than one battle destiny.");
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
