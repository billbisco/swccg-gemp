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
 * Title: You Cannot Hide Forever (V)
 */
public class Card1261_027 extends AbstractDefensiveShield {
    public Card1261_027() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "You Cannot Hide Forever (V)", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("'Give yourself to the dark side. It is the only way you can save your friends.'");
        setGameText("Plays on table. If an 'insert' card was just inserted or revealed, it is canceled. You lose no Force to Boonta Eve Podrace, and opponent's retrieval from Boonta Eve Podrace may not be canceled. While you occupy more battlegrounds than opponent, I Did It! is suspended.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }
}
