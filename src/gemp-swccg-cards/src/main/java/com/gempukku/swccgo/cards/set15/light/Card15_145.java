package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set4.light.Card4_069;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of We Can Still Outmaneuver Them. */
public class Card15_145 extends Card4_069 {
    public Card15_145() {
        setLore("");
        setCredits(3);
        setGameText("USED: Take Collision! or Egregious Pilot Error into hand from Reserve Deck; reshuffle. LOST: If opponent just initiated a battle with at least two capital starships at a system where you have a lone starfighter, add two battle destiny.");
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
        return Uniqueness.UNIQUE;
    }
}
