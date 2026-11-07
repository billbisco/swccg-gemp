package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.List;

/** Legacy Block 5 They Will Be Lost And Confused, back of printing 78. */
public class Card1250_078_BACK extends AbstractObjective {
    public Card1250_078_BACK() {
        super(Side.LIGHT, 7, Title.They_Will_Be_Lost_And_Confused, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Immediately take any one card into hand from Reserve Deck; reshuffle. While this side up, whenever a card of ability > 2 is lost, its owner loses 1 Force. While you control two battlegrounds with clones, opponent must have two cards with ability (or two [Presence] droids) to initiate a Force drain. Once per turn, may deploy a clone from Lost Pile. Clones are immune to attrition < 4. Flip this card (and if possible, relocate Palpatine to a prison and imprison him there) if Palpatine is about to be captured, lost, missing, or removed from table.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String owner = self.getOwner();
        String opponent = game.getOpponent(owner);
        var cloneBattleground = Filters.and(Filters.battleground, Filters.controlsWith(owner, self, Filters.clone));
        var insufficientCards = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard location) {
                var here = Filters.and(Filters.owner(opponent), Filters.at(location));
                return !Filters.canSpot(state.getGame(), self, 2,
                        Filters.and(here, Filters.hasAbilityOrHasPermanentPilotWithAbility))
                        && !Filters.canSpot(state.getGame(), self, 2, Filters.and(here, Filters.droid, Icon.PRESENCE));
            }
        };
        return List.of(new ImmuneToAttritionLessThanModifier(self, Filters.clone, 4),
                new MayNotForceDrainAtLocationModifier(self, insufficientCards,
                        (state, modifiers) -> Filters.countTopLocationsOnTable(game, cloneBattleground) >= 2,
                        opponent));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        var id = GameTextActionId.LEGACY__THEY_WILL_BE_LOST_AND_CONFUSED__DEPLOY_CLONE;
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.canDeployCardFromLostPile(game, playerId, self, id)) return null;

        var action = new TopLevelGameTextAction(self, gameTextSourceCardId, id);
        action.setText("Deploy a clone from Lost Pile");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new DeployCardFromLostPileEffect(action, Filters.clone, false));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.cardFlipped(game, result, self)) {
            var action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take any card into hand from Reserve Deck");
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, self.getOwner(), true));
            return List.of(action);
        }
        if (TriggerConditions.justLost(game, result, Filters.abilityMoreThan(2))) {
            var action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Owner loses 1 Force");
            action.appendEffect(new LoseForceEffect(action, ((LostFromTableResult) result).getCard().getOwner(), 1));
            return List.of(action);
        }
        return null;
    }
}
