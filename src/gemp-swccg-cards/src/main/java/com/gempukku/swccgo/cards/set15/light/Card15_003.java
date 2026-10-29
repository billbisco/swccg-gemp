package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

public class Card15_003 extends AbstractRebel {
    public Card15_003() {
        super(Side.LIGHT, 1, 4, 6, 2, 7, "Chewbacca", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Smuggler. Was attempting to repair the Millennium Falcon before the Empire located the rebel base on the planet Hoth. Known to enjoy the rug hug.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.SMUGGLER);
        addPersonas(Persona.CHEWIE);
        setSpecies(Species.WOOKIEE);
    }
}
