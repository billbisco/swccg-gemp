package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;

public class Card15_016 extends AbstractRebel {
    public Card15_016() {
        super(Side.LIGHT, 3, 3, 3, 3, 6, "Wedge", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("Rogue Squadron leader who assisted Gold Squadron One during its historic attack run on the second Death Star.");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.LEADER);
        addPersonas(Persona.WEDGE);
    }
}
