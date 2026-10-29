package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;

public class Card15_008 extends AbstractAlien {
    public Card15_008() {
        super(Side.LIGHT, 3, 2, 3, 3, 6, "Jar Jar", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("\"No, no. Mesa stay. Mesa called Jar Jar Binks. Mesa your humble servant.\"");
        setGameText("");
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addPersonas(Persona.JAR_JAR);
        setSpecies(Species.GUNGAN);
    }
}
