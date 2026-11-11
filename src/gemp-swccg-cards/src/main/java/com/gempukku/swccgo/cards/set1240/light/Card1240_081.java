package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.PlayCardAsAttachedAction;
import com.gempukku.swccgo.cards.actions.PlayCardToZoneAction;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Subtype: Immediate
 * Title: Eject! Eject! (V)
 * Printed as "Eject! Eject! & Imperial Atrocity" until the PC errata of 2013-06-14 renamed it (game text unchanged); that
 * original is 2240_81 (Bill, 2026-11-10).
 */
public class Card1240_081 extends AbstractImmediateEffect {
    public Card1240_081() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Eject_Eject, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Deploy on opponent's just deployed [Reflections III][Maintenance] card; opponent loses 1 Force, and you may retrieve 1 Force and/or play a Defensive Shield from under your Starting Effect. That [Maintenance] card is power -3, opponent must use 1 Force for that card to apply its ability towards drawing battle destiny, and, during opponent's draw phase, opponent must lose 1 Force or place that card in Lost Pile. If just lost, place this card in Used Pile. (Immune to Control.) OR If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2.");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy on opponent's just deployed card"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Deploy on table to reduce Force loss by 2"));
        return playCardOptions;
    }

    private static Filter reflections3MaintenanceCard(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Icon.REFLECTIONS_III, Icon.MAINTENANCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return reflections3MaintenanceCard(self);
    }

    private PlayCardAction playAction(String playerId, SwccgGame game, PhysicalCard self, Filter deployTargetFilter, Class<?> actionType) {
        for (PlayCardAction action : getPlayCardActions(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, deployTargetFilter, null)) {
            if (actionType.isInstance(action)) {
                return action;
            }
        }
        return null;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        List<PlayCardAction> actions = new LinkedList<PlayCardAction>();

        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.and(reflections3MaintenanceCard(self), Filters.canBeTargetedBy(self)))) {
            PhysicalCard justDeployedCard = ((PlayCardResult) effectResult).getPlayedCard();
            PlayCardAction action = playAction(playerId, game, self, Filters.sameCardId(justDeployedCard), PlayCardAsAttachedAction.class);
            if (action != null) {
                actions.add(action);
            }
        }

        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)) {
            PlayCardAction action = playAction(playerId, game, self, Filters.none, PlayCardToZoneAction.class);
            if (action != null) {
                action.appendBeforeCost(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(game.getGameState().getTopForceLossState())));
                actions.add(action);
            }
        }
        return actions;
    }

    /** True once the target's owner has used 1 Force to apply its ability towards drawing battle destiny in this battle. */
    private static boolean abilityPaidForThisBattle(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        return data != null && data.getForceLossState() == null && data.getBooleanValue();
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter target = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, target, -3));
        modifiers.add(new MayNotApplyAbilityForBattleDestinyModifier(self, target, new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return !abilityPaidForThisBattle(self);
            }
        }));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard target = self.getAttachedTo();
        if (target != null
                && playerId.equals(target.getOwner())
                && !abilityPaidForThisBattle(self)
                && GameConditions.isDuringBattleWithParticipant(game, target)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Use 1 Force to apply " + GameUtils.getCardLink(target) + "'s ability");
            action.setActionMsg("Use 1 Force to apply " + GameUtils.getCardLink(target) + "'s ability towards drawing battle destiny");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(true)));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        final PhysicalCard target = self.getAttachedTo();

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            // Deployed on table to reduce Force loss
            WhileInPlayData data = self.getWhileInPlayData();
            if (target == null && data != null && data.getForceLossState() != null) {
                ForceLossState forceLossState = data.getForceLossState();
                if (GameConditions.canReduceForceLoss(game)) {
                    RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
                    action.setPerformingPlayer(playerId);
                    action.setText("Reduce Force loss by 2");
                    action.appendUsage(
                            new OncePerForceLossEffect(action));
                    action.appendCost(
                            new SetWhileInPlayDataEffect(action, self, null));
                    action.appendEffect(
                            new ReduceForceLossEffect(action, playerId, 2, 0, false, forceLossState));
                    actions.add(action);
                }
            }
            // Deployed on opponent's card
            if (target != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Opponent loses 1 Force");
                action.setActionMsg("Make opponent lose 1 Force");
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 1));
                actions.add(action);
            }
        }

        // The ability payment lasts for one battle
        if (target != null
                && abilityPaidForThisBattle(self)
                && TriggerConditions.battleEnded(game, effectResult)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reset ability payment");
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, null));
            actions.add(action);
        }

        if (target != null
                && TriggerConditions.isStartOfOpponentsPhase(game, effectResult, Phase.DRAW, playerId)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(opponent);
            action.setText("Lose 1 Force or place " + GameUtils.getCardLink(target) + " in Lost Pile");
            action.appendEffect(
                    new PlayoutDecisionEffect(action, opponent,
                            new MultipleChoiceAwaitingDecision("Choose for " + GameUtils.getCardLink(target),
                                    new String[]{"Lose 1 Force", "Place " + target.getTitle() + " in Lost Pile"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        action.appendEffect(
                                                new LoseForceEffect(action, opponent, 1));
                                    }
                                    else {
                                        action.appendEffect(
                                                new PlaceCardInLostPileFromTableEffect(action, target));
                                    }
                                }
                            }));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        if (TriggerConditions.justDeployed(game, effectResult, self) && self.getAttachedTo() != null) {
            if (GameConditions.hasLostPile(game, playerId)) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
                action.setText("Retrieve 1 Force");
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 1));
                actions.add(action);
            }
            final PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null && GameConditions.hasStackedCards(game, startingEffect, Filters.Defensive_Shield)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_3);
                action.setText("Play a Defensive Shield");
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, startingEffect, Filters.Defensive_Shield) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                            }
                        });
                actions.add(action);
            }
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, self.getOwner(), self, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
