package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filter;

/** Solo Han (V), final Legacy Light master p36. */
public class Card1261_101 extends SoloDefenseInterrupt {
    public Card1261_101() {
        super("Solo Han", Filters.Han, true, false, GameTextActionId.LEGACY__SOLO_HAN__DEPLOY_OR_RETRIEVE);
        setLore("As a smuggler, Solo must always be alert for trouble, even when appearing relaxed. Bounty hunters are eager to claim the price on the Corellian pirate's head.");
        setGameText("Use 1 Force to deploy (or retrieve) non-[Permanent Weapon] Han (except Falcon) from Reserve Deck; reshuffle. OR If battle just initiated, and Han defending alone at a site, he is power +1 for each character and vehicle present, his forfeit cannot be reduced, he adds one destiny to power, and he draws one battle destiny if not able to otherwise. (Immune to Sense.)");
    }

    /** PC card list (PC-E1): "non-[Permanent Weapon] Han (except Falcon)": Han characters without the permanent weapon icon. */
    @Override
    protected Filter searchFilter() {
        return Filters.and(Filters.Han, Filters.character, Filters.not(Icon.PERMANENT_WEAPON));
    }

    @Override
    protected boolean bonusIsPowerNotDestiny() {
        return true;
    }
}
