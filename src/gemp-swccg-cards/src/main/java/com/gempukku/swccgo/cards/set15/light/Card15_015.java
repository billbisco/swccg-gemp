package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_015 extends AbstractAlien {
    public Card15_015() {
        super(Side.LIGHT, 3, 1, 1, 2, 3, "Tiinn", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Jawa accountant involved with tracking Coilen in and around the Dune Sea. Transmits Coilen’s activities to Tatooine officials every day; he’s still waiting to hear back from them.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        setSpecies(Species.JAWA);
    }
}
