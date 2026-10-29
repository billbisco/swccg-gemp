package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_164 extends AbstractAlien {
    public Card15_164() {
        super(Side.DARK, 2, 2, 2, 1, 3, "Gweeta", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Rodian smuggler with a reputation for selling goods that are low in supply and high in demand. Will switch her prospective buyers at a moment’s notice if the price is right.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.SMUGGLER, Keyword.FEMALE);
        setSpecies(Species.RODIAN);
    }
}
