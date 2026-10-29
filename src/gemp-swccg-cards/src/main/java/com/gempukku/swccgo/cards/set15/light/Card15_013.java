package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

public class Card15_013 extends AbstractRebel {
    public Card15_013() {
        super(Side.LIGHT, 1, 2, 3, 3, 5, "Senator Organa", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"Aren’t you a little short for a Stormtrooper?\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.SENATOR, Keyword.FEMALE);
        addPersonas(Persona.LEIA);
    }
}
