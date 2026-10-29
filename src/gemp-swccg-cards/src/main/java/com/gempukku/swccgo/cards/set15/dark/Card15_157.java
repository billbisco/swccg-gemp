package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_157 extends AbstractAlien {
    public Card15_157() {
        super(Side.DARK, 1, 5, 4, 3, 6, "Boba Fett", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Most feared bounty hunter in the entire galaxy. \"He’s no good to me dead.\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.BOUNTY_HUNTER);
        addPersonas(Persona.BOBA_FETT);
        setArmor(5);
    }
}
