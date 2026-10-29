package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.common.*;

public class Card15_309 extends AbstractCapitalStarship {
    public Card15_309() {
        super(Side.DARK, 3, 9, 9, 12, null, 2, 12, "Star Destroyer Executor", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Command ship at the Battle of Endor. Given orders to stay out of combat during the battle because the Emperor had something special planned for them.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        addPersona(Persona.EXECUTOR);
    }
}
