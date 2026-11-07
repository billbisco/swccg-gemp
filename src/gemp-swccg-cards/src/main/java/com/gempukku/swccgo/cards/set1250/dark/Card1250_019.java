package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.set1116.dark.Card1116_047;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;

/** Legacy Block 5: Nute Gunray's Bounty. */
public class Card1250_019 extends Card1116_047 {
    public Card1250_019() {
        setLore("Palpatine suggested to the Queen that she acquiesce to the Trade Federation, just for the time being.");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }
}
