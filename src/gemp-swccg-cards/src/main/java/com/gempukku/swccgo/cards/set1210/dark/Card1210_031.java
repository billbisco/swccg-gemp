package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Astromech Shortage
 */
public class Card1210_031 extends AbstractNormalEffect {
    public Card1210_031() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Astromech_Shortage, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial pilots often target astromech aboard Rebel starfighters in an attempt to prevent hyper-escapes. Scarcity of undamaged astromechs can delay starfighter deployment.");
        setGameText("Deploy on table. During the power segment of a battle, may use X Force to reduce a starship's immunity to attrition by X for remainder of turn. Once per turn, may use 2 Force to cancel the game text of a [Maintenance] character or a non-[Theed Palace] senator for remainder of turn.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId reduceImmunityId = GameTextActionId.LEGACY__ASTROMECH_SHORTAGE_V__REDUCE_IMMUNITY;
        if (TriggerConditions.justBeforePlayersDrawBattleDestiny(game, effectResult)
                && GameConditions.isDuringBattle(game)
                && !GameConditions.isDamageSegmentOfBattle(game)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, Filters.starship)) {

            final int maxForceToUse = GameConditions.forceAvailableToUse(game, playerId);
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, reduceImmunityId);
            action.setText("Reduce starship immunity to attrition");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a starship", Filters.starship) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, maxForceToUse) {
                                                @Override
                                                public void decisionMade(int result) throws DecisionResultInvalidException {
                                                    final int forceToUse = result;
                                                    action.appendCost(
                                                            new UseForceEffect(action, playerId, forceToUse));
                                                    action.allowResponses("Reduce " + GameUtils.getCardLink(targetedCard) + "'s immunity to attrition by " + forceToUse,
                                                            new UnrespondableEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    action.appendEffect(
                                                                            new AddUntilEndOfTurnModifierEffect(action,
                                                                                    new ImmunityToAttritionChangeModifier(self, Filters.sameCardId(targetedCard), -forceToUse),
                                                                                    "Reduces immunity to attrition by " + forceToUse));
                                                                }
                                                            });
                                                }
                                            }
                                    ));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId cancelTextId = GameTextActionId.LEGACY__ASTROMECH_SHORTAGE_V__CANCEL_GAME_TEXT;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, cancelTextId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTarget(game, self, Filters.or(Filters.and(Filters.character, Icon.MAINTENANCE),
                Filters.and(Filters.senator, Filters.not(Icon.THEED_PALACE))))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, cancelTextId);
            action.setText("Cancel character game text");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a [Maintenance] character or non-[Theed Palace] senator",
                            Filters.or(Filters.and(Filters.character, Icon.MAINTENANCE),
                                    Filters.and(Filters.senator, Filters.not(Icon.THEED_PALACE)))) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            action.allowResponses("Cancel game text of " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new CancelsGameTextModifier(self, Filters.sameCardId(targetedCard)),
                                                            "Cancels game text of " + GameUtils.getCardLink(targetedCard)));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        return actions;
    }
}
