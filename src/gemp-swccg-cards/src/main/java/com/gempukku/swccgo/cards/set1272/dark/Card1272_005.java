package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: Imperial Stockpile
 */
public class Card1272_005 extends AbstractNormalEffect {
    public Card1272_005() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Stockpile", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLore("The Empire's efficient, fear-driven logistical systems can quickly divert weapons and equipment from one conflict to another, focusing on regions of highest strategic priority.");
        setGameText("Deploy on table; take into hand from Reserve Deck and stack a non-lightsaber weapon (without 'lost' in game text) here; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), if it leaves table, or at end of battle. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter stockpileWeapon() {
        return Filters.and(Filters.weapon, Filters.not(Filters.lightsaber), Filters.not(Filters.gameTextContains("lost")));
    }

    /** The weapon deployed from here that is to be returned here. */
    private static PhysicalCard deployedWeapon(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        return data != null ? data.getPhysicalCard() : null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_STOCKPILE__DEPLOY_WEAPON;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && playerId.equals(self.getOwner())
                && GameConditions.hasStackedCards(game, self, Filters.weapon)) {

            final List<PhysicalCard> stackedBefore = new ArrayList<PhysicalCard>(game.getGameState().getStackedCards(self));
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy weapon on a participating card");
            action.setActionMsg("Deploy a weapon stacked on " + GameUtils.getCardLink(self) + " on a participating card");
            // Perform result(s)
            action.appendEffect(
                    new DeployStackedCardEffect(action, self, Filters.weapon, Filters.participatingInBattle, false, null, false, false) {
                    });
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            for (PhysicalCard card : stackedBefore) {
                                if (card.getZone() != null && card.getZone().isInPlay() && card.getStackedOn() == null) {
                                    self.setWhileInPlayData(new WhileInPlayData(card));
                                    return;
                                }
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Deploy on table; take a weapon from Reserve Deck and stack it here
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_STOCKPILE__STACK_WEAPON_FROM_RESERVE_DECK;
            if (GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Stack a weapon from Reserve Deck");
                action.setActionMsg("Stack a non-lightsaber weapon from Reserve Deck on " + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromReserveDeckEffect(action, self, stockpileWeapon(), true));
                return Collections.singletonList(action);
            }
            return null;
        }

        final PhysicalCard weapon = deployedWeapon(self);
        if (weapon == null)
            return null;

        // Return weapon here if it is about to leave table
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, weapon)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            if (result.getPreventableCardEffect() == null)
                return null;
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Return " + GameUtils.getFullName(weapon) + " here");
            action.setActionMsg("Return " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(weapon);
                            self.setWhileInPlayData(null);
                        }
                    });
            action.appendEffect(
                    new StackCardFromTableEffect(action, weapon, self));
            return Collections.singletonList(action);
        }

        // Return weapon here at end of weapons segment and at end of battle
        if ((TriggerConditions.justAfterWeaponsSegmentOfBattle(game, effectResult) || TriggerConditions.battleEnded(game, effectResult))) {
            if (weapon.getZone() == null || !weapon.getZone().isInPlay() || weapon.getStackedOn() != null) {
                self.setWhileInPlayData(null);
                return null;
            }
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Return " + GameUtils.getFullName(weapon) + " here");
            action.setActionMsg("Return " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(null);
                        }
                    });
            action.appendEffect(
                    new StackCardFromTableEffect(action, weapon, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
