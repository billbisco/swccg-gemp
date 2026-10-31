package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyNumCardsDrawnInStartingHandEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Krayt Dragon Howl (V)
 */
public class Card1108_010 extends AbstractUsedOrStartingInterrupt {
    public Card1108_010() {
        super(Side.LIGHT, 4, "Krayt Dragon Howl", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Using Jedi skill, Obi-Wan Kenobi imitated perfectly the mournful howl of the dangerous krayt dragon to scare Tusken Raiders away from Luke Skywalker.");
        setGameText("USED: If Obi-Wan on table, activate up to 3 Force. STARTING: [Download] Obi-Wan (regardless of Objective deployment restrictions) and his matching weapon to a Tatooine site. When you draw your starting hand, draw three cards instead of eight. Place this Interrupt in hand.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (GameConditions.canSpot(game, self, Filters.ObiWan)
                && GameConditions.canActivateForce(game, playerId)) {

            final int maxForce = Math.min(3, game.getGameState().getReserveDeckSize(playerId));
            if (maxForce > 0) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Activate up to 3 Force");
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForce, maxForce) {
                                                    @Override
                                                    public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                        action.setActionMsg("Activate " + result + " Force");
                                                        action.appendEffect(
                                                                new ActivateForceEffect(action, playerId, result));
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__KRAYT_DRAGON_HOWL__DEPLOY_OBIWAN_AND_WEAPON;
        final DeploymentRestrictionsOption ignoreObjective = new DeploymentRestrictionsOption();
        ignoreObjective.setIgnoreObjectiveDeploymentRestrictions(true);

        final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.STARTING);
        action.setText("Deploy Obi-Wan and matching weapon");
        action.allowResponses("Deploy Obi-Wan and his matching weapon to a Tatooine site from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new DeployCardToLocationFromReserveDeckEffect(action, Filters.ObiWan, Filters.Tatooine_site, false, ignoreObjective, false) {
                                    @Override
                                    protected void cardDeployed(PhysicalCard obiWan) {
                                        action.appendEffect(
                                                new DeployCardToTargetFromReserveDeckEffect(action, Filters.matchingWeaponForCharacter(obiWan), Filters.sameCardId(obiWan), false, false));
                                    }
                                });
                        action.appendEffect(
                                new ModifyNumCardsDrawnInStartingHandEffect(action, playerId, 3));
                        action.appendEffect(
                                new TakeCardFromVoidIntoHandEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}
