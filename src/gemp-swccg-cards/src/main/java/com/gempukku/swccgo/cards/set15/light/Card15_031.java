package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_045;
import com.gempukku.swccgo.common.*;

/** Reflections Gold uses the standard just-lost memorial stacking window. */
public class Card15_031 extends Card1_045 {
    public Card15_031() {
        setLore("");setCredits(2);setAsHorizontal(true);addIcons(Icon.REFLECTIONS_GOLD);
        setGameText("Deploy on table. You may place lost vehicles, droids, weapons and devices here instead of Lost Pile. Once each turn, during your deploy phase, you may exchange any one hand card with one card from your 'memorial'.");
    }
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
}
