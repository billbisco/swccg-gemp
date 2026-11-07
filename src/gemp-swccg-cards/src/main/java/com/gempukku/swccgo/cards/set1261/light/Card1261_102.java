package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.filters.Filters;

/** Warrior's Courage (V), final Legacy Light master p37. */
public class Card1261_102 extends SoloDefenseInterrupt {
    public Card1261_102() {
        super("Warrior's Courage", Filters.Leia, false, true, GameTextActionId.LEGACY__WARRIORS_COURAGE__DEPLOY_OR_RETRIEVE);
        setLore("Each Rebel soldier is driven by bravery and a belief in the Rebel Alliance's ideal of freedom. Courageous and quick-thinking Rebels often defeat Imperial legions.");
        setGameText("Use 1 Force to deploy (or retrieve) Leia from Reserve Deck; reshuffle. OR If a battle just initiated, and non-[Reflections III] Leia is defending alone at a site, her forfeit cannot be reduced, she adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
    }
}
