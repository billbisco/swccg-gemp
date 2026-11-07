package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfForceDrainModifierEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 Let's Go Left (V), final errata face. */
public class Card1250_033 extends AbstractAdmiralsOrder {
    public Card1250_033() {
        super(Side.LIGHT, "Let's Go Left", ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        setGameText("At each system a player controls with a capital starship, their non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
    }

    private Filter fleetAtSystem(PhysicalCard self, String playerId, boolean allowThreeUnique) {
        return new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard system) {
                SwccgGame game = state.getGame();
                Filter ownShipsHere = Filters.and(Filters.your(playerId), Filters.starship, Filters.at(system));
                return Filters.canSpot(game, self, Filters.and(ownShipsHere, Filters.capital_starship))
                        || (allowThreeUnique && Filters.canSpot(game, self, 3, Filters.and(ownShipsHere, Filters.unique)));
            }
        };
    }

    private Modifier deployDiscount(PhysicalCard self, String playerId) {
        Filter eligibleCards = Filters.and(Filters.your(playerId),
                Filters.or(Filters.and(Filters.character, Filters.not(Icon.PRESENCE)), Filters.vehicle));
        Filter system = Filters.and(Filters.system, Filters.controls(playerId), fleetAtSystem(self, playerId, false));
        Filter destination = Filters.or(Filters.relatedSiteTo(self, system), Filters.relatedSectorTo(self, system));
        return new DeployCostToTargetModifier(self, eligibleCards, -1, destination);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(deployDiscount(self, self.getOwner()), deployDiscount(self, game.getOpponent(self.getOwner())));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!self.getOwner().equals(playerId)) return null;

        Filter system = Filters.and(Filters.system, Filters.occupies(playerId), fleetAtSystem(self, playerId, true));
        Filter site = Filters.and(Filters.battleground, Filters.relatedSiteTo(self, system));
        if (!TriggerConditions.forceDrainInitiatedBy(game, result, playerId, site)
                || Filters.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.Objective))) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
        action.setText("Add 1 to Force drain (opponent loses no more than 2 Force)");
        action.appendEffect(new AddToForceDrainEffect(action, 1));
        action.appendEffect(new AddUntilEndOfForceDrainModifierEffect(action,
                new LimitForceLossFromForceDrainModifier(self, Filters.forceDrainLocation, 2, game.getOpponent(playerId)),
                "Opponent loses no more than 2 Force from this drain"));
        return List.of(action);
    }
}
