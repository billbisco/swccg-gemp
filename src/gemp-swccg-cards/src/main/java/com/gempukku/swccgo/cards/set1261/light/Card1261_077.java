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
 * Title: Let's Keep A Little Optimism Here (V)
 */
public class Card1261_077 extends AbstractDefensiveShield {
    public Card1261_077() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Let's Keep A Little Optimism Here (V)", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("The heroes of the Rebellion know that where there is life, there is hope.");
        setGameText("Plays on table. Ounee Ta is canceled. At opponent's Ralltiir and Renegade Planet sites, your Rebels are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }
}
