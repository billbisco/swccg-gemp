package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.*;

public class Card15_007 extends AbstractDroid {
    public Card15_007() {
        super(Side.LIGHT, 3, 2, 1, 3, "Goldenrod", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"With all we’ve been through, sometimes I’m amazed we’re in as good condition as we are, what with the Rebellion and all.\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addPersonas(Persona.C3PO);
        addModelTypes(ModelType.COMMUNICATIONS);
    }
}
