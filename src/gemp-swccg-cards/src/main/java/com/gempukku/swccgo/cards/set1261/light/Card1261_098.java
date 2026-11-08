package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;

/** Out Of Somewhere (V), final Legacy Light master p35. */
public class Card1261_098 extends SoloDefenseInterrupt {
    public Card1261_098() {
        super("Out Of Somewhere", Filters.Lando, false, false, GameTextActionId.LEGACY__OUT_OF_SOMEWHERE__DEPLOY_OR_RETRIEVE);
        setLore("When the ghost-like image of Obi-Wan failed to appear, another came out of the light to rescue Luke.");
        setGameText("Use 1 Force to deploy (or retrieve) Lando from Reserve Deck; reshuffle. OR If a battle just initiated, and your Lando defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (add 1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        addIcons(Icon.CLOUD_CITY);
    }
}
