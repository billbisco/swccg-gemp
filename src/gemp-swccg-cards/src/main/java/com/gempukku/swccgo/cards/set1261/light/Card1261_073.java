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
 * Title: Your Insight Serves You Well (V)
 */
public class Card1261_073 extends AbstractDefensiveShield {
    public Card1261_073() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Your Insight Serves You Well (V)", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Luke knew that while the dark side was quicker and more seductive, eventually evil would turn on itself.");
        setGameText("Plays on table. Scanning Crew is canceled. If an 'insert' card was just inserted or revealed, it is canceled. You lose no Force to Boonta Eve Podrace, and opponent's retrieval from Boonta Eve Podrace may not be canceled. While you occupy three battlegrounds, Watto's Box is suspended.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }
}
