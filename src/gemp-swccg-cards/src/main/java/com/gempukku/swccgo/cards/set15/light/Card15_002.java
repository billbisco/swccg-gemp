package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.common.*;

public class Card15_002 extends AbstractRepublic {
    public Card15_002() {
        super(Side.LIGHT, 2, 3, 1, 4, 6, "Anakin Skywalker", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Vader. \"He can help you.\" \"You refer to the prophecy of the one who will bring balance to the Force.\" \"The boy is dangerous. They all sense it. Why can’t you?\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD, Icon.PILOT, Icon.EPISODE_I);
        addKeywords(Keyword.SKYWALKER);
        addPersonas(Persona.ANAKIN);
    }
}
