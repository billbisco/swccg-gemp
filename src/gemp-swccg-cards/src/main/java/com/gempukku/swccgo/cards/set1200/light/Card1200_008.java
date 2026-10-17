package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;

/**
 * Set: vshields
 * Type: Defensive Shield
 * Title: Don't Do That Again (V)
 */
public class Card1200_008 extends AbstractDefensiveShield {
    public Card1200_008() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Don't Do That Again (V)", ExpansionSet.VIRTUAL_SHIELDS, Rarity.V);
        setLore("The Jedi won't tolerate silly behavior for very long.");
        setGameText("Plays on table. Always Thinking With Your Stomach, Ice Storm, and Sandwhirl are canceled. Once per game, may /\\ an Immediate Effect. Unless opponent occupies a battleground system, Mobilization Points is suspended.");
        addIcons(Icon.EPISODE_I, Icon.LEGACY_BLOCK_D);
        setAsLegacy(true);
    }
}
