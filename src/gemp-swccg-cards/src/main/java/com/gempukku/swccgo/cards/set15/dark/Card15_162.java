package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;

public class Card15_162 extends AbstractImperial {
    public Card15_162() {
        super(Side.DARK, 1, 3, 4, 3, 5, "Governor Tarkin", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Commander of the original Death Star. Tarkin will stop at nothing to ensure the destruction of the Rebellion at Yavin IV. Despiser of non-human races.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.COMMANDER);
        addPersonas(Persona.TARKIN);
    }
}
