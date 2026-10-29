package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMaster;
import com.gempukku.swccgo.common.*;

public class Card15_160 extends AbstractDarkJediMaster {
    public Card15_160() {
        super(Side.DARK, 1, 5, 4, 7, 9, "Emperor", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"Soon you will call me master.\"");
        setGameText("");
        addIcons(Icon.IMPERIAL, Icon.REFLECTIONS_GOLD);
        addCardType(CardType.IMPERIAL);
        addPersonas(Persona.SIDIOUS);
    }
}
