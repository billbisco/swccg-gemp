package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set8.dark.Card8_117;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Aratech Corporation. */
public class Card15_171 extends Card8_117 {
    public Card15_171() {
        setLore("");
        setCredits(2);
        setGameText("Deploy on Carida system. Your speeder bikes piloted by biker scouts are power +1 and forfeit +1 and may follow (for free) an opponent's vehicle or character that just moved from same site (if within range). (Immune to Alter while you occupy Carida.)");
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
