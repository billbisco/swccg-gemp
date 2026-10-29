package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set12.dark.Card12_147;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Dioxis. */
public class Card15_234 extends Card12_147 {
    public Card15_234() {
        setLore("");
        setCredits(2);
        setGameText("Use 2 Force to target two opponent's Jedi at same interior battleground. Targets may not participate in battle for remainder of turn. OR Target opponent's Jedi. Draw destiny. If destiny > 2, target's game text is canceled for remainder of turn.");
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
