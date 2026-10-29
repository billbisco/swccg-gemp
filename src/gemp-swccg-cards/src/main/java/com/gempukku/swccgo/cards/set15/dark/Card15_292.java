package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set4.dark.Card4_150;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Those Rebels Won't Escape Us. */
public class Card15_292 extends Card4_150 {
    public Card15_292() {
        setLore("");
        setCredits(1);
        setGameText("USED: Cancel Nabrun Leids. LOST: During your move phase, cancel Landing Claw. OR Cancel Hyper Escape, Closer?! or one 'react.'");
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
