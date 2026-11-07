package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.set1109.dark.Card1109_050;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;

/** Legacy Block 5 reuses the predecessor's complete printed gameplay. */
public class Card1250_025 extends Card1109_050 {
    public Card1250_025() {
        setGameText("Deploy on a site. Unless a player controls a battleground site, that player's [Permanent Weapon] destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)");
        addIcon(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() { return ExpansionSet.VIRTUAL_BLOCK_5; }
}
