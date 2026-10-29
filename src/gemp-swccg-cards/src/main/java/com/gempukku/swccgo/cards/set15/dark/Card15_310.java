package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.common.*;

public class Card15_310 extends AbstractCombatVehicle {
    public Card15_310() {
        super(Side.DARK, 4, 2, 3, 4, null, 3, 4, "Blizzard Scout 4", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("AT-ST walker assigned to the left flank at the Battle of Hoth. One of the first Imperial units to break through the Rebel defense perimeter. Enclosed.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addModelType(ModelType.AT_ST);
        addKeyword(Keyword.ENCLOSED);
    }
}
