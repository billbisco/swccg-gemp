package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set13.dark.Card13_058;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Blow Parried. */
public class Card15_223 extends Card13_058 {
    public Card15_223() {
        setLore("");
        setCredits(1);
        setGameText("Take top card of Force Pile into hand. OR Cancel a just-revealed combat card (place in opponent's Used Pile) and opponent must draw destiny instead (that destiny is reduced by 1). OR Cancel Glancing Blow, Clash Of Sabers, or Obi-Wan's Journal.");
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
