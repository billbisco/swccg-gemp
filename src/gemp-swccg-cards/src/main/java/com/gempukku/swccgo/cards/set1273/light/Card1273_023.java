package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.PeekAtRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Antilles
 */
public class Card1273_023 extends AbstractRebel {
    public Card1273_023() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Captain Antilles", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setGameText("Adds 2 to the power of anything he pilots, 3: any corvette. While piloting a corvette, it is immune to attrition < 5 and, once per turn, may peek at a card in opponent's hand. Once per game, during battle (unless Vader here) may use 2 Force to move starship he pilots away (for free).");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.CAPTAIN);
        setMatchingStarshipFilter(Filters.corvette);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.corvette)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.corvette, Filters.hasPiloting(self)),
                new PilotingCondition(self, Filters.corvette), 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        String opponent = game.getOpponent(playerId);

        // Once per turn, while piloting a corvette, may peek at a card in opponent's hand
        GameTextActionId peekActionId = GameTextActionId.LEGACY__CAPTAIN_ANTILLES__PEEK_AT_CARD_IN_HAND;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, peekActionId)
                && GameConditions.isPiloting(game, self, Filters.corvette)
                && GameConditions.hasHand(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, peekActionId);
            action.setText("Peek at a card in opponent's hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtRandomCardInOpponentsHandEffect(action, playerId));
            actions.add(action);
        }

        // Once per game, during battle (unless Vader here), may use 2 Force to move starship he pilots away (for free)
        GameTextActionId moveAwayActionId = GameTextActionId.LEGACY__CAPTAIN_ANTILLES__MOVE_STARSHIP_AWAY;
        final Filter starshipHePilots = Filters.and(Filters.starship, Filters.hasPiloting(self), Filters.participatingInBattle);
        if (GameConditions.isOncePerGame(game, self, moveAwayActionId)
                && GameConditions.isDuringBattle(game)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.here(self)))
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canSpot(game, self, Filters.and(starshipHePilots, Filters.movableAsMoveAway(playerId, true, 0, Filters.any)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, moveAwayActionId);
            action.setText("Move starship away");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new MoveCardsAwayEffect(action, playerId, starshipHePilots, Filters.any, false, true, true));
            actions.add(action);
        }
        return actions;
    }
}
