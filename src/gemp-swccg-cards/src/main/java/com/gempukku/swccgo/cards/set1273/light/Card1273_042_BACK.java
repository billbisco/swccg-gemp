package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyVb73Light;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Objective
 * Title: Center Of Tyranny / A Liberated World
 */
public class Card1273_042_BACK extends AbstractObjective {
    public Card1273_042_BACK() {
        super(Side.LIGHT, 7, LegacyVb73Light.A_LIBERATED_WORLD, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setGameText(Card1273_042.SHEET_GAMETEXT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        Condition youOccupyASystem = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return GameConditions.occupies(gameState.getGame(), playerId, Filters.system);
            }
        };
        Filter relatedBattlegroundSitesYouControlWithRogueSquadronPilot = Filters.and(Filters.battleground_site,
                Filters.controlsWith(playerId, self, Filters.Rogue_Squadron_pilot),
                Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.Coruscant_system, 2, playerId));
        modifiers.add(new ForceDrainModifier(self, relatedBattlegroundSitesYouControlWithRogueSquadronPilot, youOccupyASystem, 1, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Immediately retrieve into hand (or take from Reserve Deck) any one card
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve (or take from Reserve Deck) any one card into hand");
            // Perform result(s)
            final boolean canTake = GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, GameTextActionId.LEGACY__A_LIBERATED_WORLD__TAKE_CARD_INTO_HAND);
            if (canTake) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new MultipleChoiceAwaitingDecision("Choose", new String[]{"Retrieve a card into hand", "Take a card into hand from Reserve Deck"}) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        if (index == 0) {
                                            action.appendEffect(
                                                    new RetrieveCardIntoHandEffect(action, playerId));
                                        }
                                        else {
                                            action.appendEffect(
                                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, true));
                                        }
                                    }
                                }));
            }
            else {
                action.appendEffect(
                        new RetrieveCardIntoHandEffect(action, playerId));
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        String opponent = game.getOpponent(playerId);

        // Once per turn, if opponent just lost a starship from a system, may lose 1 Force to steal it into hand
        GameTextActionId stealActionId = GameTextActionId.LEGACY__A_LIBERATED_WORLD__STEAL_STARSHIP;
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.owner(opponent), Filters.starship), Filters.system)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, stealActionId)) {
            final PhysicalCard starship = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, stealActionId);
            action.setText("Steal " + GameUtils.getFullName(starship) + " into hand");
            action.setActionMsg("Steal " + GameUtils.getCardLink(starship) + " into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.sameCardId(starship)));
            actions.add(action);
        }

        // Once during your turn, if you just forfeited a Rogue Squadron pilot, may use 1 Force to take any one card
        GameTextActionId takeActionId = GameTextActionId.LEGACY__A_LIBERATED_WORLD__TAKE_CARD_INTO_HAND;
        if (TriggerConditions.justForfeited(game, effectResult, playerId, Filters.Rogue_Squadron_pilot)
                && GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, takeActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, takeActionId);
            action.setText("Take any one card into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, true));
            actions.add(action);
        }
        return actions;
    }
}
