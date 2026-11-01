package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Effect
 * Title: Dark Reconnaissance (V)
 */
public class Card1115_044 extends AbstractNormalEffect {
    public Card1115_044() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Dark Reconnaissance", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("On her assignment to kill Sharad Hett, Aurra used her patience and cunning to help track down the Jedi Master.");
        setGameText("Deploy on opponent's location. Wherever you have two Mistryl, your Force drains are +1. 'Stolen' on Swilla Corey is treated as 'placed in owner's Used Pile' and it may target a weapon instead of a device. Your Force drains here cannot be modified or canceled by opponent.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addKeyword(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.location);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter twoMistryl = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.location.accepts(gameState, modifiersQuerying, physicalCard)
                        && Filters.countActive(gameState.getGame(), self, Filters.and(Filters.your(playerId), Filters.Mistryl, Filters.at(physicalCard))) >= 2;
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, twoMistryl, 1, playerId));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, Filters.here(self), opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), opponent, playerId));
        return modifiers;
    }
}
