package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Effect
 * Title: Order 66
 */
public class Card1116_048 extends AbstractNormalEffect {
    public Card1116_048() {
        super(Side.DARK, 7, PlayCardZoneOption.ATTACHED, "Order 66", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Deploy on Sidious. If with [Virtual Set 16] Expand The Empire during your control phase: relocate this card to an [Episode I] Jedi at a battleground or Senate (otherwise, retrieve 1 Force). If on a Jedi, add one battle destiny here, Jedi may not use Force, and Jedi is lost at end of turn.");
        addIcons(Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Sidious;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.<Modifier>singletonList(new AddsBattleDestinyModifier(self, new AttachedCondition(self, Filters.Jedi), 1));
    }
}
