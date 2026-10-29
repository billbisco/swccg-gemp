package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set9.light.Card9_036;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Launching The Assault. */
public class Card15_043 extends Card9_036 {
    public Card15_043() {
        setLore("");
        setCredits(3);
        setGameText("Deploy on a system. Once per game, you may take Home One into hand from Reserve Deck; reshuffle. Force drains are +1 at battleground systems where you have a Star Cruiser piloted by a character. Effect canceled if Rendezvous Point on table. (Immune to Alter.)");
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
