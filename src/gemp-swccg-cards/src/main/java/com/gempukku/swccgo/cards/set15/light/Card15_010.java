package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

public class Card15_010 extends AbstractRebel {
    public Card15_010() {
        super(Side.LIGHT, 2, 2, 1, 3, 4, "Owen", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Only Owen Lars knew what kind of impact Anakin’s past would have on Luke. That’s what he was afraid of.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addPersonas(Persona.OWEN);
    }
}
