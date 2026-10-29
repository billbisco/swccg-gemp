package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set6.light.Card6_076;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Strangle. */
public class Card15_135 extends Card6_076 {
    public Card15_135() {
        setLore("");
        setCredits(1);
        setGameText("Target an escort alone and its captive. Draw destiny. Escort lost if destiny + captive's power > escort's ability + power. OR During a battle at a site where Leia is an escorted captive, add one battle destiny.");
        setAlternateDestiny(7);
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
        return 7f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
