package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Evacuate? (V)
 */
public class Card1273_015 extends AbstractUsedOrLostInterrupt {
    public Card1273_015() {
        super(Side.DARK, 6, "Evacuate?", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Escape pods are on many starships allowing those in peril to flee, an act considered cowardly by Imperial officers. 'We've analyzed their attack, sir, and there is a danger.'");
        setGameText("Use 1 Force to target your character of ability > 3 alone at a site. USED: During any move phase, take target into hand. LOST: If opponent has exactly one non-spy character at target's site, place target in Used Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter target = Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3), Filters.alone, Filters.at(Filters.site));

        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTarget(game, self, target)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Take character into hand");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", target) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            action.allowResponses("Take " + GameUtils.getCardLink(targetedCard) + " into hand",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new ReturnCardToHandFromTableEffect(action, finalTarget));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        final Filter targetFacingOneNonSpy = Filters.and(target, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard site = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
                if (site == null) {
                    return false;
                }
                return Filters.countActive(gameState.getGame(), self, Filters.and(Filters.opponents(self), Filters.character,
                        Filters.not(Filters.spy), Filters.atLocation(site))) == 1;
            }
        });

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTarget(game, self, targetFacingOneNonSpy)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place character in Used Pile");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFacingOneNonSpy) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in Used Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
