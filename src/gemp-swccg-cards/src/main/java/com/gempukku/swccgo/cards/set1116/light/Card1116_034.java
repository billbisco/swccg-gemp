package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Droid
 * Title: Lars' Protocol Droid
 */
public class Card1116_034 extends AbstractDroid {
    public Card1116_034() {
        super(Side.LIGHT, 4, 2, 1, 3, "Lars' Protocol Droid", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        addModelType(ModelType.PROTOCOL);
        setGameText("'Device' on Harvest may be treated as '[A New Hope] or [Hoth] droid.' While at a farm: your characters present are forfeit +3; may raise this site (if converted) to the top; and, once per move phase, may put hand in Used Pile to place a 'crashed' card out of play to cancel a Force drain at a related site.");
    }
}
