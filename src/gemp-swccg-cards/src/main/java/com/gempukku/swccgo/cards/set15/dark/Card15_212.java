package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractBlankEffect;
import com.gempukku.swccgo.common.*;

public class Card15_212 extends AbstractBlankEffect {
    public Card15_212() {
        super(Side.DARK, 4f, "Visage Of A Viceroy", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("They must be dead by now. Destroy what’s left of them. Hologram.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addKeywords(Keyword.HOLOGRAM);
    }
}
