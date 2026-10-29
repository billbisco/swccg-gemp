package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_012 extends AbstractAlien {
    public Card15_012() {
        super(Side.LIGHT, 4, 3, 3, 1, 3, "Rakes Jevorik", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Mercenary who turned against Jabba when Luke Skywalker and his friends destroyed his sail barge at the great pit of carkoon. Now seeking a new employer.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.MERCENARY);
    }
}
