package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.set1112.dark.Card1112_031;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Species;

/** Virtual Block 5 reprinting of Daultay Dofine (V). */
public class Card1250_005 extends Card1112_031 {
    public Card1250_005() {
        setGameText("Adds 3 to power of anything he pilots. Blockade Flagship is immune to attrition < 9. While piloting Blockade Flagship, if your [Theed Palace] Admiral's Order on table, Blockade Flagship may not be targeted by weapons or have its power reduced. Once per game, may take Gunray or Haako into hand from Reserve Deck; reshuffle.");
        setSpecies(Species.NEIMOIDIAN);
        addIcon(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }
}
