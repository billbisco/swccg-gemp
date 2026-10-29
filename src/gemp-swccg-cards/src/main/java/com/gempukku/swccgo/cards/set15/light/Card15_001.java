package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.*;

public class Card15_001 extends AbstractDroid {
    public Card15_001() {
        super(Side.LIGHT, 3, 2, 0, 5, "2-1B", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Member of the droid pool on the rebel base of Hoth. Worked on Luke Skywalker and other members of Rogue Squadron who needed medical treatment.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addModelTypes(ModelType.MEDICAL);
    }
}
