package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Imperial Tyranny
 */
public class Card1109_038 extends AbstractUsedOrLostInterrupt {
    public Card1109_038() {
        super(Side.DARK, 4, Title.Imperial_Tyranny, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire considers alien species to be inferior.");
        setGameText("USED: Deploy Empire's New Order on table from Reserve Deck; reshuffle. OR If Vader just won a battle at the same site as an alien, place that alien out of play. LOST: If a battle was just initiated where you have a character weapon card, place the top card of your Lost Pile on top of your Reserve Deck.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId enoId = GameTextActionId.LEGACY__IMPERIAL_TYRANNY_V__DEPLOY_EMPIRES_NEW_ORDER;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, enoId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, enoId, CardSubtype.USED);
            action.setText("Deploy Empire's New Order from Reserve Deck");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Empires_New_Order, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.and(Filters.site, Filters.sameSiteAs(self, Filters.Vader)))
                && GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.participatingInBattle))) {
            Filter alienAtSite = Filters.and(Filters.alien, Filters.sameSiteAs(self, Filters.Vader));
            if (GameConditions.canTarget(game, self, alienAtSite)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Place alien out of play");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose alien", alienAtSite) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                action.appendEffect(
                                                        new PlaceCardOutOfPlayFromTableEffect(action, finalTarget));
                                            }
                                        }
                                );
                            }
                        });
                actions.add(action);
            }
        }

        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character_weapon))
                && GameConditions.hasLostPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place top Lost Pile card on Reserve Deck");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard topLost = game.getGameState().getTopOfCardPile(playerId, Zone.LOST_PILE);
                            if (topLost != null) {
                                action.appendEffect(
                                        new PutCardFromLostPileOnTopOfCardPileEffect(action, topLost, Zone.RESERVE_DECK, false));
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }
}
