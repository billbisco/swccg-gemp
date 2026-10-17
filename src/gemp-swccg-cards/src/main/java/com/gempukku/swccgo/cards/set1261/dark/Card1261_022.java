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
 * Title: No Escape (V)
 */
public class Card1261_022 extends AbstractDefensiveShield {
    public Card1261_022() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "No Escape (V)", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Jabba's influence is not easily ignored. Neither are his voracious and vile appetites. Even Jedi soon learn this lesson.");
        setGameText("Plays on table. While you occupy a Subjugated Planet location, operatives are forfeit = 0, operatives do not add to Force drains, and your Force drains may not be reduced. Nudjs do not cancel Force icons.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }
}
