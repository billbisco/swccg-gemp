package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardToTargetEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: Rebel Gunrunner
 */
public class Card1272_022 extends AbstractNormalEffect {
    public Card1272_022() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Rebel Gunrunner", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        addIcons(Icon.PREMIUM);
        setLore("Smugglers from many worlds are hunted by the Empire for providing arms and supplies to the Alliance. The Outer Rim is their refuge.");
        setGameText("Deploy on table; take and stack a non-lightsaber weapon (without 'lost' in game text) here from Reserve Deck; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), if it leaves table, or at end of battle. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    static Filter weaponToStack() {
        return Filters.and(Filters.weapon, Filters.not(Filters.lightsaber), Filters.not(Filters.gameTextContains("lost")));
    }

    static Filter participatingTarget(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.participatingInBattle);
    }

    /** The weapon deployed from here that is to be returned here, if it is still on table. */
    private static PhysicalCard deployedWeapon(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        if (data == null || data.getPhysicalCard() == null)
            return null;
        PhysicalCard weapon = data.getPhysicalCard();
        if (weapon.getZone() == null || !weapon.getZone().isInPlay())
            return null;
        return weapon;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REBEL_GUNRUNNER__STACK_WEAPON;

        // Deploy on table; take and stack a weapon here from Reserve Deck
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack a weapon from Reserve Deck");
            action.setActionMsg("Stack a non-lightsaber weapon on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new StackCardFromReserveDeckEffect(action, playerId, self, weaponToStack(), true, false));
            return Collections.singletonList(action);
        }

        final PhysicalCard weapon = deployedWeapon(self);
        if (weapon == null)
            return null;

        // Return weapon here if it leaves table
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, weapon)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Return " + GameUtils.getFullName(weapon) + " here");
            action.setActionMsg("Return " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (result.getPreventableCardEffect() != null) {
                                result.getPreventableCardEffect().preventEffectOnCard(weapon);
                            }
                            self.setWhileInPlayData(null);
                        }
                    });
            action.appendEffect(
                    new StackCardFromTableEffect(action, weapon, self));
            return Collections.singletonList(action);
        }

        // Return weapon here at end of weapons segment (if possible), or at end of battle
        if (TriggerConditions.justAfterWeaponsSegmentOfBattle(game, effectResult)
                || TriggerConditions.battleEnded(game, effectResult)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REBEL_GUNRUNNER__DEPLOY_WEAPON;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && deployedWeapon(self) == null) {
            final Filter targetFilter = participatingTarget(self);
            List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
            for (final PhysicalCard weapon : game.getGameState().getStackedCards(self)) {
                if (Filters.and(Filters.weapon, Filters.deployableToTarget(self, targetFilter, false, 0)).accepts(game, weapon)) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Deploy " + GameUtils.getFullName(weapon) + " on a participating card");
                    action.setActionMsg("Deploy " + GameUtils.getCardLink(weapon) + " from " + GameUtils.getCardLink(self) + " on a participating card");
                    // Perform result(s)
                    action.appendEffect(
                            new DeployStackedCardToTargetEffect(action, weapon, targetFilter, false));
                    action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    if (weapon.getZone() != null && weapon.getZone().isInPlay() && weapon.getAttachedTo() != null) {
                                        self.setWhileInPlayData(new WhileInPlayData(weapon));
                                    }
                                }
                            });
                    actions.add(action);
                }
            }
            return actions;
        }
        return null;
    }
}
