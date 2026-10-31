package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Vehicle
 * Subtype: Combat
 * Title: Chewie's AT-ST
 */
public class Card1109_002 extends AbstractCombatVehicle {
    public Card1109_002() {
        super(Side.LIGHT, 4, 2, 3, 4, null, 3, 5, "Chewie's AT-ST", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Enclosed. Chewie's daring capture of an Imperial AT-ST helped turn the tide in the Battle of Endor.");
        setGameText("May add 2 pilots. During battle, if Chewie aboard, may target an opponent's character or AT-ST present. Draw destiny. Target hit and power = 0 if destiny > defense value. Immune to attrition < 4.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(2);
        setMatchingPilotFilter(Filters.Chewie);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        TargetingReason targetingReason = TargetingReason.TO_BE_HIT;
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.present(self), Filters.or(Filters.character, Filters.AT_ST));

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.hasAboard(game, self, Filters.Chewie)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Target character or AT-ST");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character or AT-ST", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Draw destiny while targeting " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(gameState, finalTarget);
                                                            gameState.sendMessage("Destiny: " + totalDestiny + "  Defense value: " + defenseValue);
                                                            if (totalDestiny > defenseValue) {
                                                                gameState.sendMessage("Result: Hit");
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, finalTarget, self));
                                                                action.appendEffect(
                                                                        new ResetPowerUntilEndOfBattleEffect(action, finalTarget, 0));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
