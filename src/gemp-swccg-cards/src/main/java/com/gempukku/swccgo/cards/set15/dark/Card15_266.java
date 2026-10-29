package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set2.dark.Card2_137;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

public class Card15_266 extends Card2_137 {
    public Card15_266() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(5);
        setLore("");
        setGameText("If Nabrun Leids just completed a transport, use 2 Force. Nabrun is lost and all Rebels transported are captured. (Immune to Sense.) OR Prevent a just-deployed smuggler from moving this turn. OR Cancel a Kessel Run.");
        setCredits(1);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Oo-ta Goo-ta Solo?"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNRESTRICTED; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


}
