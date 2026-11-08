package com.gempukku.swccgo.cards.set1240.dark;

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
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Effect
 * Subtype: Immediate
 * Title: He Is Not Ready & Imperial Propaganda
 */
public class Card1240_026 extends AbstractImmediateEffect {
    public Card1240_026() {
        super(Side.DARK, 5, null, "He Is Not Ready & Imperial Propaganda", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        addComboCardTitles(Title.He_Is_Not_Ready, Title.Imperial_Propaganda);
        setGameText("Deploy on opponent's just deployed [Reflections III][Maintenance] card; opponent loses 1 Force, and you may retrieve 1 Force and/or play a Defensive Shield from under your Starting Effect. That card is power -3, may not apply its ability towards drawing battle destiny, and, during opponent's draw phase, opponent must lose 1 Force or place that card in Lost Pile. If just lost, place this card in Used Pile. Immune to Honor Of The Jedi. [Immune to Control] OR If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2.");
        addIcons(Icon.DAGOBAH, Icon.LEGACY_BLOCK_4);
        addImmuneToCardTitle(Title.Control);
        addImmuneToCardTitle(Title.Honor_Of_The_Jedi);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        return Arrays.asList(
                new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy on Maintenance card"),
                new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Reduce Force loss by 2"));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId optionId, boolean asReact) {
        return optionId == PlayCardOptionId.PLAY_CARD_OPTION_1
                ? Filters.and(Filters.opponents(self), Icon.REFLECTIONS_III, Icon.MAINTENANCE)
                : Filters.none;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<PlayCardAction> actions = new LinkedList<>();
        if (TriggerConditions.justDeployed(game, effectResult, game.getOpponent(playerId),
                Filters.and(Filters.opponents(self), Icon.REFLECTIONS_III, Icon.MAINTENANCE, Filters.canBeTargetedBy(self)))) {
            PhysicalCard host = ((PlayCardResult) effectResult).getPlayedCard();
            for (PlayCardAction action : getPlayCardActions(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(host), null)) {
                if (action instanceof PlayCardAsAttachedAction) {
                    actions.add(action);
                }
            }
        }
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canReduceForceLoss(game)) {
            for (PlayCardAction action : getPlayCardActions(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null)) {
                if (action instanceof PlayCardToZoneAction) {
                    action.setText("Reduce Force loss by 2");
                    action.appendBeforeCost(new SetWhileInPlayDataEffect(action, self,
                            new WhileInPlayData(game.getGameState().getTopForceLossState())));
                    actions.add(action);
                }
            }
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Arrays.asList(
                new PowerModifier(self, Filters.hasAttached(self), -3),
                new MayNotApplyAbilityForBattleDestinyModifier(self, Filters.hasAttached(self)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();
        PhysicalCard host = self.getAttachedTo();
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            if (host != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Opponent loses 1 Force");
                action.appendEffect(new LoseForceEffect(action, game.getOpponent(playerId), 1));
                actions.add(action);
            } else if (self.getWhileInPlayData() != null
                    && self.getWhileInPlayData().getForceLossState() != null
                    && GameConditions.canReduceForceLoss(game)) {
                ForceLossState forceLossState = self.getWhileInPlayData().getForceLossState();
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
                action.setText("Reduce Force loss by 2");
                action.appendUsage(new OncePerForceLossEffect(action));
                action.appendCost(new SetWhileInPlayDataEffect(action, self, null));
                action.appendEffect(new ReduceForceLossEffect(action, playerId, 2, 0, false, forceLossState));
                actions.add(action);
            }
        }
        if (host != null && TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.DRAW)) {
            String hostOwner = host.getOwner();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(hostOwner);
            action.setText("Pay extra maintenance");
            action.appendEffect(new PlayoutDecisionEffect(action, hostOwner,
                    new MultipleChoiceAwaitingDecision("Choose extra maintenance option", new String[]{"Lose 1 Force", "Place target in Lost Pile"}) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            if (index == 0) {
                                action.appendEffect(new LoseForceEffect(action, hostOwner, 1));
                            } else {
                                action.appendEffect(new LoseCardFromTableEffect(action, host));
                            }
                        }
                    }));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (self.getAttachedTo() == null || !TriggerConditions.justDeployed(game, effectResult, self)) {
            return null;
        }
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        if (GameConditions.hasLostPile(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
            actions.add(action);
        }
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null && GameConditions.hasStackedCards(game, startingEffect, Filters.and(Filters.Defensive_Shield, Filters.playable(self)))) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Play a Defensive Shield");
            action.appendEffect(new PlayStackedDefensiveShieldEffect(action, startingEffect));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, self.getOwner(), self, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
