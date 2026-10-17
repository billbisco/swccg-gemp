package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;

/**
 * Set: VirtualBlock6_1
 * Type: Defensive Shield
 * Title: Ounee Ta (V)
 */
public class Card1261_072 extends AbstractDefensiveShield {
    public Card1261_072() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ounee Ta (V)", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Jabba's decadent behavior makes him susceptible to deception. Leia and Lando exploited this weakness, posing as Jabba's kind of scum.");
        setGameText("Plays on table. While you occupy a Renegade planet location, operatives are forfeit = 0, operatives do not add to Force drains and your Force drains may not be reduced. Sleens do not cancel Force icons. Cannot be canceled.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }
}
