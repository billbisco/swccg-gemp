package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.ShuffleLostPileEffect;
import com.gempukku.swccgo.logic.effects.ShuffleUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Objective
 * Title: Infiltration / Unlikely Allies
 */
public class Card1281_044_BACK extends AbstractObjective {
    public Card1281_044_BACK() {
        super(Side.LIGHT, 7, "Unlikely Allies", ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setGameText(Card1281_044.PACK_GAMETEXT);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(playerId), Filters.scoundrel), 1));
        modifiers.add(new TotalForceGenerationModifier(self,
                new MinEvaluator(
                        new OccupiesEvaluator(playerId, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.scoundrel))),
                        new ConstantEvaluator(2)),
                playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.title("Scoundrel's Luck"), ModifyGameTextType.LEGACY__SCOUNDRELS_LUCK__ONCE_IS_TWICE));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        List<TopLevelGameTextAction> battleground = Card1281_044.battlegroundSiteAction(playerId, game, self, gameTextSourceCardId);
        if (battleground != null) {
            actions.addAll(battleground);
        }

        GameTextActionId retrieveId = GameTextActionId.LEGACY__UNLIKELY_ALLIES__RETRIEVE_FORCE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, retrieveId, Phase.CONTROL)
                && GameConditions.hasLostPile(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, retrieveId);
            action.setText("Retrieve 1 Force");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
            actions.add(action);
        }

        GameTextActionId placeId = GameTextActionId.LEGACY__UNLIKELY_ALLIES__PLACE_CARD_AND_TAKE_TOP;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, placeId)
                && GameConditions.hasHand(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, placeId);
            action.setText("Place a card from hand in Used or Lost Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("Place a card from hand in", new String[]{"Used Pile", "Lost Pile"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        action.appendEffect(new PutCardFromHandOnUsedPileEffect(action, playerId));
                                        action.appendEffect(new ShuffleUsedPileEffect(action, self, playerId));
                                        action.appendEffect(new DrawCardIntoHandFromUsedPileEffect(action, playerId));
                                    }
                                    else {
                                        action.appendEffect(new PutCardFromHandOnLostPileEffect(action, playerId));
                                        action.appendEffect(new ShuffleLostPileEffect(action, self, playerId));
                                        action.appendEffect(new DrawCardIntoHandFromLostPileEffect(action, playerId));
                                    }
                                }
                            }));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, Filters.title("Imperial Navigation Charts"))
                && !GameConditions.hasCompletedUtinniEffect(game, playerId, virtualKesselRun())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(new FlipCardEffect(action, self));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }

    private Filter virtualKesselRun() {
        return Filters.and(Filters.Kessel_Run, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().hasVirtualSuffix();
            }
        });
    }
}
