package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set6.light.Card6_061;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Blaster Deflection. */
public class Card15_067 extends Card6_061 {
    public Card15_067() {
        setLore("");
        setCredits(2);
        setGameText("USED: Cancel an attempt to use a character weapon to target your character of ability > 4. LOST: If your character of ability > 4 with a lightsaber was just targeted by a blaster, use 3 Force to re-target that blaster to an opponent's character present.");
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
