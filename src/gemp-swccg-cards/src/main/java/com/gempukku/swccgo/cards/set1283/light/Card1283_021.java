package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayRetrieveForceWithCardOnlyOncePerTurnModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Effect
 * Title: Underworld Contacts & Uncharted Settlements
 */
public class Card1283_021 extends AbstractNormalEffect {
    public Card1283_021() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, "Underworld Contacts & Uncharted Settlements", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setGameText("Deploy on [Virtual Block 4] Hidden Base; you must choose alien/[Independent] starship. You may not deploy Ellors Madak, Revolution or operatives. Your Destiny is canceled. Opponent generates no Force at Hidden Landing Site. [Virtual Block 1] Yerka Mig may deploy on a Rebel Cell Site, may be placed in Used Pile if it was just lost, and you may retrieve Force with it only once per turn. While Systems Will Slip Through Your Fingers on table, attrition against opponent is +1 for each Rebel Cell site you occupy with an alien. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Hidden_Base, Icon.LEGACY_BLOCK_4);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Hidden_Base, Filters.title("Systems Will Slip Through Your Fingers"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter yerkaV = Filters.and(Filters.title("Yerka Mig"), Icon.LEGACY_BLOCK_1);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.Ellorrs_Madak, Filters.Revolution, Filters.operative), playerId));
        modifiers.add(new GenerateNoForceModifier(self, Filters.titleContains("Rebel Cell - Hidden Landing Site"), opponent));
        modifiers.add(new MayDeployToLocationModifier(self, yerkaV, Filters.titleContains("Rebel Cell")));
        modifiers.add(new MayRetrieveForceWithCardOnlyOncePerTurnModifier(self, yerkaV));
        modifiers.add(new AttritionModifier(self, Filters.any,
                new OnTableCondition(self, Filters.title("Systems Will Slip Through Your Fingers")),
                new RebelCellOccupiedWithAlienEvaluator(self, playerId), opponent, true));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter toCancel = Filters.Your_Destiny;
        if (TriggerConditions.isPlayingCard(game, effect, toCancel)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard hiddenBase = self.getAttachedTo();
            if (hiddenBase != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Choose alien/Independent starship");
                action.appendEffect(new SetWhileInPlayDataEffect(action, hiddenBase, new WhileInPlayData("Alien")));
                actions.add(action);
            }
        }
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Your_Destiny)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Your_Destiny, Title.Your_Destiny);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter yerkaV = Filters.and(Filters.your(playerId), Filters.title("Yerka Mig"), Icon.LEGACY_BLOCK_1);

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, yerkaV)) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justLostCard) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    private static class RebelCellOccupiedWithAlienEvaluator extends BaseEvaluator {
        private final int _sourcePermId;
        private final String _playerId;

        private RebelCellOccupiedWithAlienEvaluator(PhysicalCard source, String playerId) {
            _sourcePermId = source.getPermanentCardId();
            _playerId = playerId;
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
            SwccgGame game = gameState.getGame();
            PhysicalCard source = game.findCardByPermanentId(_sourcePermId);
            int count = 0;
            for (PhysicalCard site : Filters.filterTopLocationsOnTable(game, Filters.titleContains("Rebel Cell"))) {
                if (Filters.canSpot(game, source, Filters.and(Filters.your(_playerId), Filters.alien, Filters.at(site)))) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
            return evaluateExpression(gameState, modifiersQuerying, cardAffected);
        }
    }
}
