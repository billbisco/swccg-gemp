package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;

/**
 * Set: VirtualBlock6_1
 * Type: Defensive Shield
 * Title: Leave Them To Me (V)
 */
public class Card1261_021 extends AbstractDefensiveShield {
    public Card1261_021() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Leave Them To Me (V)", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("'I will deal with them myself.'");
        setGameText("Plays on table. At opponent's Dantooine and Subjugated Planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }
}
