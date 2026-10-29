package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set11.dark.Card11_084;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Pit Crews. */
public class Card15_270 extends Card11_084 {
    public Card15_270() {
        setLore("");
        setCredits(1);
        setGameText("Use 3 Force to reveal opponent's hand. All cards opponent has 3 or more of in hand are lost. OR Use 1 Force to 'repair' your Podracer. OR Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.'");
        setAlternateDestiny(4);
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
        return 4f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
