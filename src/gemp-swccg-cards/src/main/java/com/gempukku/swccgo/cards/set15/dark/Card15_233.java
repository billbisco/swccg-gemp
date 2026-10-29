package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set1.dark.Card1_241;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Dark Maneuvers. */
public class Card15_233 extends Card1_241 {
    public Card15_233() {
        setLore("");
        setCredits(1);
        setGameText("Add 2 to maneuver and 1 to power of any starfighter for remainder of turn. If it has hyperdrive, also add 2 to hyperspeed. (Interrupt may even affect the result immediately after a destiny draw targeting the starfighter's maneuver.)");
        setAlternateDestiny(6);
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
        return 6f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNRESTRICTED;
    }
}
