package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.filters.Filters;

/** Solo Han (V), final Legacy Light master p36. */
public class Card1261_101 extends SoloDefenseInterrupt {
    public Card1261_101() {
        super("Solo Han", Filters.Han, true, false, GameTextActionId.LEGACY__SOLO_HAN__DEPLOY_OR_RETRIEVE);
        setLore("As a smuggler, Solo must always be alert for trouble, even when appearing relaxed. Bounty hunters are eager to claim the price on the Corellian pirate's head.");
        setGameText("Use 1 Force to deploy (or retrieve) non-[Reflections III] Han from Reserve Deck; reshuffle. OR If a battle just initiated, and Han is defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
    }
}
