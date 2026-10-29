package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_158 extends AbstractAlien {
    public Card15_158() {
        super(Side.DARK, 3, 1, 2, 2, 2, "Coilen", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Leader of the Jawa pack who captured R2-D2 on Tatooine and a nefarious thief. Nemesis to Rachalt Hyst - both have stolen goods from one another on multiple occasions.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.LEADER, Keyword.THIEF);
        setSpecies(Species.JAWA);
    }
}
