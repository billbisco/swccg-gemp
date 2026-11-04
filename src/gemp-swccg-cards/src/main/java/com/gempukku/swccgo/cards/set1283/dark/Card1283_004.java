package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Character
 * Subtype: Sith
 * Title: Luuke
 */
public class Card1283_004 extends AbstractSith {
    public Card1283_004() {
        super(Side.DARK, 1, 6, 5, 5, 5, "Luuke", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLore("Assassin and clone.");
        setGameText("Adds 2 to the power of anything he pilots. May use any stolen lightsaber. During battle, may steal a lightsaber from opponent's Lost Pile (return weapon to opponent's Lost Pile if it is about to leave table or at end of battle). May use 2 Force to cancel Luke's game text here for remainder of turn.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ASSASSIN, Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayUseWeaponModifier(self, Filters.and(Filters.stolen, Filters.lightsaber)));
        return modifiers;
    }

    /** Lightsabers Luuke stole from opponent's Lost Pile that are still to be returned. */
    private static List<PhysicalCard> stolenByLuuke(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        if (data == null || data.getPhysicalCards() == null)
            return new ArrayList<PhysicalCard>();
        return new ArrayList<PhysicalCard>(data.getPhysicalCards());
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LUUKE__STEAL_LIGHTSABER_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canSearchOpponentsLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal lightsaber from Lost Pile");
            action.setActionMsg("Steal a lightsaber from opponent's Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new StealCardAndAttachFromLostPileEffect(action, playerId, self, Filters.lightsaber));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            List<PhysicalCard> tracked = stolenByLuuke(self);
                            for (PhysicalCard card : Filters.filterAllOnTable(game, Filters.and(Filters.stolen, Filters.lightsaber, Filters.attachedTo(self)))) {
                                if (!tracked.contains(card)) {
                                    tracked.add(card);
                                }
                            }
                            self.setWhileInPlayData(new WhileInPlayData(tracked));
                        }
                    });
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final Filter lukeHere = Filters.and(Filters.Luke, Filters.here(self), Filters.not(Filters.isGameTextCanceled));

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTarget(game, self, lukeHere)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Luke's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Luke", lukeHere) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text for remainder of turn",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, action.getPrimaryTargetCard(targetGroupId)));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final List<PhysicalCard> tracked = stolenByLuuke(self);
        if (tracked.isEmpty())
            return null;

        final String opponent = game.getOpponent(self.getOwner());

        // If Luuke is about to leave table, the stolen weapons on him go with him: they go to opponent's Lost Pile
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Return stolen lightsaber to opponent's Lost Pile");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            for (PhysicalCard weapon : stolenByLuuke(self)) {
                                weapon.setOwner(opponent);
                            }
                            self.setWhileInPlayData(null);
                        }
                    });
            return Collections.singletonList(action);
        }

        // Return weapon to opponent's Lost Pile if it is about to leave table
        for (final PhysicalCard weapon : tracked) {
            if (TriggerConditions.isAboutToLeaveTable(game, effectResult, weapon)) {
                if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_LOST_FROM_TABLE
                        || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE) {
                    // Already going to a Lost Pile: make it opponent's
                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Return " + GameUtils.getFullName(weapon) + " to opponent's Lost Pile");
                    action.setActionMsg(null);
                    action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    weapon.setOwner(opponent);
                                    List<PhysicalCard> remaining = stolenByLuuke(self);
                                    remaining.remove(weapon);
                                    self.setWhileInPlayData(new WhileInPlayData(remaining));
                                }
                            });
                    return Collections.singletonList(action);
                }
                final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Return " + GameUtils.getFullName(weapon) + " to Lost Pile");
                action.setActionMsg("Return " + GameUtils.getCardLink(weapon) + " to opponent's Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (result.getPreventableCardEffect() != null) {
                                    result.getPreventableCardEffect().preventEffectOnCard(weapon);
                                }
                                List<PhysicalCard> remaining = stolenByLuuke(self);
                                remaining.remove(weapon);
                                self.setWhileInPlayData(new WhileInPlayData(remaining));
                                // Returned to its owner's (opponent's) Lost Pile
                                weapon.setOwner(opponent);
                            }
                        });
                action.appendEffect(
                        new PlaceCardInLostPileFromTableEffect(action, weapon));
                return Collections.singletonList(action);
            }
        }

        // Return weapon to opponent's Lost Pile at end of battle
        if (TriggerConditions.battleEnded(game, effectResult)) {
            final Collection<PhysicalCard> onTable = new ArrayList<PhysicalCard>();
            for (PhysicalCard weapon : tracked) {
                if (weapon.getZone() != null && weapon.getZone().isInPlay()) {
                    onTable.add(weapon);
                }
            }
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Return stolen lightsaber to Lost Pile");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(null);
                            // Returned to its owner's (opponent's) Lost Pile
                            for (PhysicalCard weapon : onTable) {
                                weapon.setOwner(opponent);
                            }
                        }
                    });
            if (!onTable.isEmpty()) {
                action.setActionMsg("Return " + GameUtils.getAppendedNames(onTable) + " to opponent's Lost Pile");
                action.appendEffect(
                        new PlaceCardsInLostPileFromTableEffect(action, onTable, false, Zone.LOST_PILE));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
