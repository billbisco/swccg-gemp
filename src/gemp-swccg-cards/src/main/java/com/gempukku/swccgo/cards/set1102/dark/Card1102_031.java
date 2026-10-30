package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Interrupt
 * Subtype: Used
 * Title: Emergency Deployment
 */
public class Card1102_031 extends AbstractUsedInterrupt {
    public Card1102_031() {
        super(Side.DARK, 3, Title.Emergency_Deployment, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("TIE fighters are stored in large racks far above the hangar deck. Catapulted into space by powerful tractor beam generators, TIEs can quickly scramble to engage the enemy.");
        setGameText("Take a device or a non-lightsaber weapon into hand from Reserve Deck; reshuffle. OR If opponent just initiated a battle or Force drain at a system or sector, deploy (as a 'react') an unpiloted TIE and a pilot of ability < 3 (simultaneously) on table from Reserve Deck; reshuffle. (Immune to Sense.)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__EMERGENCY_DEPLOYMENT_V__TAKE_DEVICE_OR_WEAPON;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take device or weapon into hand");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Take a device or a non-lightsaber weapon into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                                            Filters.or(Filters.device, Filters.and(Filters.weapon, Filters.not(Filters.lightsaber))), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__EMERGENCY_DEPLOYMENT_V__DEPLOY_TIE_AND_PILOT;

        if ((TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.system_or_sector)
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.system_or_sector))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy TIE and pilot as 'react'");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Deploy an unpiloted TIE and a pilot of ability < 3 as a 'react' from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ChooseCardFromReserveDeckEffect(action, playerId, Filters.and(Filters.pilot, Filters.abilityLessThan(3))) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                            action.appendEffect(
                                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard,
                                                            Filters.and(Filters.TIE, Filters.unpiloted), true, true));
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
