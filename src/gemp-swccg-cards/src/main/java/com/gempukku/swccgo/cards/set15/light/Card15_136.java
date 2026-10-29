package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set13.light.Card13_043;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Strike Blocked. */
public class Card15_136 extends Card13_043 {
    public Card15_136() {
        setLore("");
        setCredits(2);
        setGameText("Take top card of Force Pile into hand. OR If opponent just revealed a combat card, it is canceled (place in opponent's Used Pile) and opponent must draw destiny instead (that destiny is reduced by 1). OR Cancel Maul Strikes, Dark Strike, or You Are Beaten.");
        setAlternateDestiny(3);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
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
        return Uniqueness.UNIQUE;
    }
}
