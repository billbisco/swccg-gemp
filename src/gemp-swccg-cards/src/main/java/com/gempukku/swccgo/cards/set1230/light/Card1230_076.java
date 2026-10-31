package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInForcePileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Rebel
 * Title: Master Luke
 */
public class Card1230_076 extends AbstractRebel {
    public Card1230_076() {
        super(Side.LIGHT, 1, 5, 5, 5, 7, Title.Master_Luke, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Until being reunited with Yoda, Luke suspected that he had completed his training. Has a strong influence on the weak minded.");
        setGameText("Adds 2 to power of anything he pilots. During battle, may use 2 Force to place an opponent's alien (except Jabba or a Rep) present with Luke in owner's Force Pile (opponent may use or lose 3 Force instead). Immune to attrition < 4 (< 7 if with an alien leader).");
        addPersona(Persona.LUKE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PREMIUM);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new CardMatchesEvaluator(4, 7,
                Filters.presentWith(self, Filters.and(Filters.alien, Filters.leader)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MASTER_LUKE_V__PLACE_ALIEN;
        final String opponent = game.getOpponent(playerId);
        Filter alienFilter = Filters.and(Filters.opponents(self), Filters.alien, Filters.presentWith(self), Filters.not(Filters.Jabba));
        PhysicalCard oppRep = game.getGameState().getRep(opponent);
        if (oppRep != null) {
            alienFilter = Filters.and(alienFilter, Filters.not(Filters.sameCardId(oppRep)));
        }

        if (GameConditions.isDuringBattle(game)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTarget(game, self, TargetingReason.OTHER, alienFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place alien in Force Pile");
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            final Filter finalAlienFilter = alienFilter;
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose alien", TargetingReason.OTHER, finalAlienFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in owner's Force Pile",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            final String USE_FORCE = "Use 3 Force instead";
                                            final String LOSE_FORCE = "Lose 3 Force instead";
                                            final String PLACE = "Place alien in Force Pile";
                                            List<String> options = new ArrayList<String>();
                                            if (GameConditions.canUseForce(game, opponent, 3)) {
                                                options.add(USE_FORCE);
                                            }
                                            options.add(LOSE_FORCE);
                                            options.add(PLACE);
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, opponent,
                                                            new MultipleChoiceAwaitingDecision("Opponent may use or lose 3 Force instead", options.toArray(new String[0])) {
                                                                @Override
                                                                protected void validDecisionMade(int index, String result) {
                                                                    if (USE_FORCE.equals(result)) {
                                                                        game.getGameState().sendMessage(opponent + " chooses to use 3 Force instead");
                                                                        action.appendEffect(
                                                                                new UseForceEffect(action, opponent, 3));
                                                                    }
                                                                    else if (LOSE_FORCE.equals(result)) {
                                                                        game.getGameState().sendMessage(opponent + " chooses to lose 3 Force instead");
                                                                        action.appendEffect(
                                                                                new LoseForceEffect(action, opponent, 3, true));
                                                                    }
                                                                    else {
                                                                        action.appendEffect(
                                                                                new PlaceCardInForcePileFromTableEffect(action, finalTarget));
                                                                    }
                                                                }
                                                            }
                                                    )
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
