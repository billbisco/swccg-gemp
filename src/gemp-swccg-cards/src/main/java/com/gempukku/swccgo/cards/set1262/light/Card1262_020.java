package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Beast-Rider (V), final Virtual Block 6.2 text. */
public class Card1262_020 extends AbstractRepublic {
    public Card1262_020() {
        super(Side.LIGHT, 2, 3, 2, 4, 5, "Beast-Rider", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true); setLore("Female.");
        setGameText("Landspeed = 2. During battle, for each non-parasite creature here (even if excluded), power +2 (+4 if ferocious). If an opponent's card here is eaten, you may retrieve up to 4 Force. Once per turn, may deploy Kaadu here from Reserve Deck; reshuffle. Cannot be attacked.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I); addKeyword(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DefinedByGameTextLandspeedModifier(self, 2));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        BaseEvaluator creatureBonus = new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                float bonus = 0;
                for (PhysicalCard creature : Filters.filterActive(gameState.getGame(), self,
                        SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE,
                        Filters.and(Filters.creature, Filters.not(Filters.and(Keyword.PARASITE)), Filters.here(self)))) {
                    // Historical ferocious definition; current non-parasite is a broader category.
                    boolean ferocious = modifiersQuerying.getDefenseValue(gameState, creature) > 2
                            && !Filters.title("Mynock").accepts(gameState, modifiersQuerying, creature)
                            && !Filters.title("Vine Snake").accepts(gameState, modifiersQuerying, creature);
                    bonus += ferocious ? 4 : 2;
                }
                return bonus;
            }
        };
        return Arrays.asList(new PowerModifier(self, new InBattleCondition(self), creatureBonus),
                new MayNotBeAttackedModifier(self));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId actionId = GameTextActionId.LEGACY__BEAST_RIDER__DEPLOY_KAADU;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, actionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, actionId, true, false)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, actionId);
            action.setText("Deploy Kaadu here from Reserve Deck"); action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardToLocationFromReserveDeckEffect(action, Filters.Kaadu, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (playerId.equals(self.getOwner())
                && TriggerConditions.justEatenBy(game, effectResult, Filters.opponents(self), Filters.any)
                && Filters.sameLocation(self).accepts(game, ((EatenResult) effectResult).getEatenAtLocation())) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve up to 4 Force");
            action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                    new IntegerAwaitingDecision("Choose amount of Force to retrieve", 0, 4, 4) {
                        @Override
                        public void decisionMade(int amount) {
                            if (amount > 0) action.appendEffect(new RetrieveForceEffect(action, playerId, amount));
                        }
                    }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
