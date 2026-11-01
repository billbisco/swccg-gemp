package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Interrupt
 * Subtype: Used
 * Title: It's Worse
 */
public class Card1116_055 extends AbstractUsedInterrupt {
    public Card1116_055() {
        super(Side.DARK, 6, "It's Worse", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Use 1 Force to deploy a pit. OR At end of your deploy phase, target a non-frozen captive at a site; captive is released and relocated to a related pit you do not occupy. OR During an attack, cancel a just drawn weapon destiny.");
        addIcons(Icon.SPECIAL_EDITION);
    }
}
