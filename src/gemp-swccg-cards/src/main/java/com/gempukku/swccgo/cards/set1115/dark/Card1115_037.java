package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Abyssin Ornament & Wounded Wookiee
 */
public class Card1115_037 extends AbstractUsedOrStartingInterrupt {
    public Card1115_037() {
        super(Side.DARK, 2, "Abyssin Ornament & Wounded Wookiee", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        addComboCardTitles(Title.Abyssin_Ornament, Title.Wounded_Wookiee);
        setVirtualSuffix(true);
        setGameText("USED: Creature attacks at pits are immune to At Peace and Yarna d'al' Gargan until end of your next turn. (Immune to Sense) OR Cancel an attempt to target a rancor with a character weapon. OR Use 1 Force (2 if a rancor on table) to deploy Nal Hutta from Reserve Deck; reshuffle. STARTING: If Great Pit Of Carkoon on table, deploy from Reserve Deck an Effect that is always [Immune to Alter] and choose to deploy from Reserve Deck: Rancor Pit and a rancor there, OR Power Of The Hutt and Sarlacc. Place this Interrupt in hand.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final PlayInterruptAction immuneAction = new PlayInterruptAction(game, self, CardSubtype.USED);
        immuneAction.setImmuneTo(Title.Sense);
        immuneAction.setText("Make creature attacks at pits immune");
        immuneAction.allowResponses("Make creature attacks at pits immune to At Peace and Yarna d'al' Gargan until end of your next turn",
                new RespondablePlayCardEffect(immuneAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        immuneAction.appendEffect(
                                new AddUntilEndOfPlayersNextTurnModifierEffect(immuneAction, playerId,
                                        new ImmuneToTitleModifier(self, Filters.and(Filters.creature, Filters.at(Filters.pit)), Title.At_Peace),
                                        "makes creature attacks at pits immune to At Peace"));
                        immuneAction.appendEffect(
                                new AddUntilEndOfPlayersNextTurnModifierEffect(immuneAction, playerId,
                                        new ImmuneToTitleModifier(self, Filters.and(Filters.creature, Filters.at(Filters.pit)), Title.Yarna_dal_Gargan),
                                        "makes creature attacks at pits immune to Yarna d'al' Gargan"));
                    }
                }
        );
        actions.add(immuneAction);

        GameTextActionId nalHuttaId = GameTextActionId.LEGACY__ABYSSIN_ORNAMENT_WOUNDED_WOOKIEE_V__DEPLOY_NAL_HUTTA;
        int cost = GameConditions.canSpot(game, self, Filters.Rancor) ? 2 : 1;
        if (GameConditions.canUseForce(game, playerId, cost)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, nalHuttaId)) {
            final int forceCost = cost;
            final PlayInterruptAction action = new PlayInterruptAction(game, self, nalHuttaId, CardSubtype.USED);
            action.setText("Deploy Nal Hutta from Reserve Deck");
            action.appendCost(new UseForceEffect(action, playerId, forceCost));
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Nal_Hutta_system, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.Rancor, Filters.character_weapon)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel attempt to target a rancor");
            action.allowResponses("Cancel an attempt to target a rancor with a character weapon",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        if (!GameConditions.canSpotLocation(game, Filters.Great_Pit_Of_Carkoon)) {
            return null;
        }
        GameTextActionId startingId = GameTextActionId.LEGACY__ABYSSIN_ORNAMENT_WOUNDED_WOOKIEE_V__STARTING;
        final PlayInterruptAction action = new PlayInterruptAction(game, self, startingId, CardSubtype.STARTING);
        action.setText("Deploy starting cards from Reserve Deck");
        action.allowResponses("Deploy an always Immune to Alter Effect, then Rancor Pit and a rancor or Power Of The Hutt and Sarlacc",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 1, true, false));
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Rancor_Pit, Filters.title(Title.Power_Of_The_Hutt)), true, false) {
                                    @Override
                                    protected void cardDeployed(PhysicalCard card) {
                                        if (Filters.Rancor_Pit.accepts(game, card)) {
                                            action.appendEffect(
                                                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Rancor, Filters.sameCardId(card), true, false));
                                        }
                                        else {
                                            action.appendEffect(
                                                    new DeployCardFromReserveDeckEffect(action, Filters.Sarlacc, true, false));
                                        }
                                    }
                                });
                        action.appendEffect(
                                new TakeCardFromVoidIntoHandEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}
