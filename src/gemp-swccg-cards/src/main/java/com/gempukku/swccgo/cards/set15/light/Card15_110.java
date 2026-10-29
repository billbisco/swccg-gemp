package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set3.light.Card3_046;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Nice Of You Guys To Drop By. */
public class Card15_110 extends Card3_046 {
    public Card15_110() {
        setLore("");
        setCredits(1);
        setGameText("Send one of your missing characters at same site as one of your piloted or driven vehicles to your Used Pile. OR Cancel any attempt by one character to 'hide' from a battle.");
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
