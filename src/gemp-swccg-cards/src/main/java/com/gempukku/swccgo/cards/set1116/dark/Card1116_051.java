package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Droid
 * Title: 3B3-10 (V)
 */
public class Card1116_051 extends AbstractDroid {
    public Card1116_051() {
        super(Side.DARK, 2, 2, 3, 3, "3B3-10", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setArmor(3);
        addModelType(ModelType.BATTLE);
        addKeyword(Keyword.HEROIC);
        setGameText("Heroic. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). During your move phase, may use 1 Force to 'break cover' of an Undercover spy here. 3B3-10's forfeit may not be reduced.");
    }
}
