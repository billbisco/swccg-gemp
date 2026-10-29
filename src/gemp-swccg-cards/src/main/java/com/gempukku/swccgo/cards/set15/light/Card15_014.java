package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

public class Card15_014 extends AbstractRebel {
    public Card15_014() {
        super(Side.LIGHT, 1, 3, 3, 3, 7, "Solo", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"I’m captain of the Millennium Falcon. Chewie tells me you’re looking for passage to the Alderaan system.\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addPersonas(Persona.HAN);
    }
}
