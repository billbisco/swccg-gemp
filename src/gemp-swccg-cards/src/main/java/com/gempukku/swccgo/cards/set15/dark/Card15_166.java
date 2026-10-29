package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.*;

public class Card15_166 extends AbstractDroid {
    public Card15_166() {
        super(Side.DARK, 2, 1, 3, 3, "SSA-33", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Infantry battle droid assigned guard duty in the Theed Palace docking bay. Experimented with brand new security subroutines that were to be used in the newer model battle droids.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addModelTypes(ModelType.BATTLE);
        setArmor(3);
    }
}
