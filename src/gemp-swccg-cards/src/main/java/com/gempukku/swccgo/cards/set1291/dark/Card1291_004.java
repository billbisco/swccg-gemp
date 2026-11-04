package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Khurgee (V)
 */
public class Card1291_004 extends AbstractImperial {
    public Card1291_004() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Captain Khurgee", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Docking bay security officer. Leader. Honored for bravery aboard the Star Destroyer Thunderflare where he rescued five officers from the wreckage of a shuttle crash.");
        setGameText("Adds 2 to the power of anything he pilots. While at a battleground system, once during your turn may peek at top card of opponent's Reserve Deck; if it is a Rebel, may lose 1 Force to make it lost. Forfeit +2 (to a maximum of 7) aboard Thunderflare or with a leader.");
        addIcons(Icon.WARRIOR, Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
        addPersona(Persona.KHURGEE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        final Modifier[] forfeitBonus = new Modifier[1];
        forfeitBonus[0] = new ForfeitModifier(self, self,
                new OrCondition(new AboardCondition(self, Filters.Thunderflare), new WithCondition(self, Filters.and(Filters.other(self), Filters.leader))),
                new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                        Float printed = cardAffected.getBlueprint().getForfeit();
                        float otherwise = printed != null ? printed : 0;
                        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.FORFEIT_VALUE, cardAffected)) {
                            if (modifier != forfeitBonus[0]) {
                                otherwise += modifier.getForfeitModifier(gameState, modifiersQuerying, cardAffected);
                            }
                        }
                        // Forfeit +2, to a maximum of 7
                        return Math.max(0, Math.min(2, 7 - otherwise));
                    }
                });
        modifiers.add(forfeitBonus[0]);
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CAPTAIN_KHURGEE_V__PEEK_AT_TOP_OF_RESERVE_DECK;
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.battleground_system)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top card of opponent's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                        @Override
                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                            if (Filters.Rebel.accepts(game, peekedAtCard)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Lose 1 Force to make " + GameUtils.getCardLink(peekedAtCard) + " lost?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new LoseForceEffect(action, playerId, 1, true));
                                                        action.appendEffect(
                                                                new LoseCardFromTopOfReserveDeckEffect(action, opponent, peekedAtCard));
                                                    }
                                                }));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
