package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_006 extends AbstractAlien {
    public Card15_006() {
        super(Side.LIGHT, 3, 3, 1, 2, 2, "Ewok Tribesman", Uniqueness.RESTRICTED_3, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Tribesmen are often in charge of making critical decisions that affect an entire tribe. Allowing Han Solo and his strike team into their village was one of those decisions.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        setSpecies(Species.EWOK);
    }
}
