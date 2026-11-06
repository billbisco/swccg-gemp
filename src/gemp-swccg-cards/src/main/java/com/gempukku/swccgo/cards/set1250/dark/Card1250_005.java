package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.set1112.dark.Card1112_031;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;

/** Virtual Block 5 reprinting of Daultay Dofine (V). */
public class Card1250_005 extends Card1112_031 {
    public Card1250_005() {
        addIcon(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }
}
