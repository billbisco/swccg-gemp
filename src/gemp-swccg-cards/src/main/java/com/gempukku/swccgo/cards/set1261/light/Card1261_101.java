package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/** Solo Han (V), final Legacy Light master p36.
 * Text: PC-E3, the Virtual Cards Master List (2013-04-27), which repeats the release-sheet words (non-[Reflections III] Han; the +1 per character and vehicle present goes to the added destiny); the
 * PC website card list text (PC-E1, 2012) is version 2261_1101 and the PC card image (PC-E2, by 2013-02-25, no comma after "present)") is 2261_2101
 * (Bill 2026-11-11: every publication is a version; an image is placed at its proven upper bound).
 */
public class Card1261_101 extends SoloDefenseInterrupt {
    public Card1261_101() {
        super("Solo Han", Filters.Han, true, false, GameTextActionId.LEGACY__SOLO_HAN__DEPLOY_OR_RETRIEVE);
        setLore("As a smuggler, Solo must always be alert for trouble, even when appearing relaxed. Bounty hunters are eager to claim the price on the Corellian pirate's head.");
        setGameText("Use 1 Force to ▼ (or retrieve) non-[Reflections III] Han. OR If a battle just initiated, and Han defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
    }

    /** PC-E3 (Master List = 2009 sheet): "non-[Reflections III] Han": any Han (character, or a starship with Han as permanent pilot) without the [Reflections III] icon. */
    @Override
    protected Filter searchFilter() {
        Filter hanOnPhysicalPrinting = Filters.or(Filters.Han, Filters.hasPermanentAboard(Filters.Han));
        return new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard card) {
                return !card.getBlueprint().hasIcon(Icon.REFLECTIONS_III) && hanOnPhysicalPrinting.accepts(state, querying, card);
            }
        };
    }

    @Override
    protected boolean bonusIsPowerNotDestiny() {
        return false;
    }
}
