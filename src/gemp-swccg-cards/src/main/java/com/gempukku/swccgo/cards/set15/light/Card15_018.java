package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

public class Card15_018 extends AbstractRebel {
    public Card15_018() {
        super(Side.LIGHT, 1, 3, 3, 4, 8, "Young Skywalker", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"Luke’s just not a farmer, Owen. He has too much of his father in him.\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.SKYWALKER);
        addPersonas(Persona.LUKE);
    }
}
