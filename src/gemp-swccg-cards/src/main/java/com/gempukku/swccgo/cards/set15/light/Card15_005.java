package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_005 extends AbstractAlien {
    public Card15_005() {
        super(Side.LIGHT, 3, 3, 2, 3, 4, "Drem Redins", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Ishi Tib gambler responsible for handling and paying out on small wagers at Boonta Eve. Rumored to be losing money because of his odds not being as desirable as Jabba’s.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addKeywords(Keyword.GAMBLER);
        setSpecies(Species.ISHI_TIB);
    }
}
