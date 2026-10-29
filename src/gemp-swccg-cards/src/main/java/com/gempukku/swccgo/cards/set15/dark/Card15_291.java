package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set12.dark.Card12_161;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

public class Card15_291 extends Card12_161 {
    public Card15_291() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(4);
        setLore("");
        setGameText("Cancel Rebel Barrier, It's A Trap!, or Smoke Screen. OR Re-target Fallen Portal by choosing up to two of opponent's characters there instead of your own. OR Peek at the top card of your Force Pile; either return it, or place it under your Force Pile or Reserve Deck.");
        setCredits(2);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
    }
    @Override public String getTitle() { return "They're Still Coming Through"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNRESTRICTED; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


}
