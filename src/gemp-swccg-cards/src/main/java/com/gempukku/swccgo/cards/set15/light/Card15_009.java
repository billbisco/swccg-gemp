package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

public class Card15_009 extends AbstractRebel {
    public Card15_009() {
        super(Side.LIGHT, 1, 5, 5, 6, 9, "Old Ben Kenobi", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"Hello there! Come here my little friend, don’t be afraid.\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addPersonas(Persona.OBIWAN);
    }
}
