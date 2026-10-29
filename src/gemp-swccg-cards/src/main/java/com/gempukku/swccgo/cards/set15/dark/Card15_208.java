package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set8.dark.Card8_132;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Security Precautions. */
public class Card15_208 extends Card8_132 {
    public Card15_208() {
        setLore("");
        setCredits(4);
        setGameText("Deploy on a location. Your Force generation is +1 here. If you 'probe' opponent's \"Hidden Base\" system, opponent loses X Force; X = 15 - number of systems on table. You take no battle damage where you have a 'probe' card. (Immune to Alter.)");
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
        return Uniqueness.UNIQUE;
    }
}
