package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.common.*;

public class Card15_011 extends AbstractRepublic {
    public Card15_011() {
        super(Side.LIGHT, 2, 4, 2, 4, 6, "Palpatine, Senator To Naboo", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"A tragedy has occurred, which started right here with the taxation of trade routes and has now engulfed our entire planet in the oppression of the Trade Federation.\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
        addPersonas(Persona.SIDIOUS);
        setPolitics(4);
    }
}
