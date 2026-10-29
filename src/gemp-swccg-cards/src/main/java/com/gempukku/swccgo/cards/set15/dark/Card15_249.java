package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set9.dark.Card9_137;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Imperial Command. */
public class Card15_249 extends Card9_137 {
    public Card15_249() {
        setLore("");
        setCredits(3);
        setGameText("Take one admiral or general into hand from Reserve Deck; reshuffle. OR If your admiral is in battle at a system (or your general is in battle at a site) either add one battle destiny or prevent opponent from drawing more than one battle destiny.");
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
