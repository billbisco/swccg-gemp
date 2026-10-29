package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_087;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of How Did We Get Into This Mess?. */
public class Card15_086 extends Card1_087 {
    public Card15_086() {
        setLore("");
        setCredits(3);
        setGameText("If one of your droids is at the Dune Sea or where there is a Scomp link, use 1 Force to discard your entire hand to your Used Pile. Draw the same number of cards from your Reserve Deck. Draw one extra card if droid is C-3PO or R2-D2.");
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
