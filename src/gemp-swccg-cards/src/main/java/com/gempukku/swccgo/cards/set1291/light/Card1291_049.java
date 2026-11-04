package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Effect
 * Title: A Grand Army Of The Republic
 */
public class Card1291_049 extends AbstractNormalEffect {
    public Card1291_049() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Grand Army Of The Republic", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("");
        setGameText("Deploy on table. Once per turn, if you just deployed a combat vehicle, may draw top card of Reserve Deck. During your deploy phase, may use 1 Force and reveal one [Clone Army] vehicle from hand to take into hand a clone pilot of ability < 3 (or vice versa) from Reserve Deck; reshuffle and deploy both simultaneously. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    static Filter cloneArmyVehicle() {
        return Filters.and(Icon.CLONE_ARMY, Filters.vehicle);
    }

    static Filter clonePilot() {
        return Filters.and(Filters.clone, Filters.pilot, Filters.abilityLessThan(3));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_GRAND_ARMY_OF_THE_REPUBLIC__DRAW_CARD;
        if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.combat_vehicle)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter vehicle = cloneArmyVehicle();
        final Filter pilot = clonePilot();
        Filter toReveal = Filters.and(Filters.or(vehicle, pilot), Filters.isUniquenessOnTableNotReached);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_GRAND_ARMY_OF_THE_REPUBLIC__DEPLOY_VEHICLE_AND_CLONE_PILOT;

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasInHand(game, playerId, toReveal)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal [Clone Army] vehicle or clone pilot from hand");
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, toReveal) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (vehicle.accepts(game, selectedCard)) {
                                action.setActionMsg("Take a clone pilot of ability < 3 from Reserve Deck and deploy both simultaneously");
                                searchFilter = pilot;
                            } else {
                                action.setActionMsg("Take a [Clone Army] vehicle from Reserve Deck and deploy both simultaneously");
                                searchFilter = vehicle;
                            }
                            action.appendCost(new UseForceEffect(action, playerId, 1));
                            action.appendEffect(
                                    new SendMessageEffect(action, playerId + " reveals " + GameUtils.getCardLink(selectedCard) + " with " + GameUtils.getCardLink(self)));
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
