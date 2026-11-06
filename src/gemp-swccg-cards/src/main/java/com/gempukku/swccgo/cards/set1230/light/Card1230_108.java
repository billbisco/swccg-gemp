package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveOrBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Lost
 * Title: Rapid Deployment
 */
public class Card1230_108 extends AbstractLostInterrupt {
    public Card1230_108() {
        super(Side.LIGHT, 5, "Rapid Deployment", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("General Solo's strike team was made up of the Alliance's finest ground troops.");
        setGameText("Target a site. At same and related sites, Rebel scouts deploying from hand are deploy -2 (and they may not battle or move) for remainder of turn. OR Deploy a Rebel scout (deploy +2) to a site from Reserve Deck; reshuffle.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter siteFilter = Filters.and(Filters.site, Filters.canBeTargetedBy(self));
        if (GameConditions.canTarget(game, self, siteFilter)) {
            final PlayInterruptAction discount = new PlayInterruptAction(game, self);
            discount.setText("Reduce Rebel scout deploy cost");
            discount.appendTargeting(
                    new TargetCardOnTableEffect(discount, playerId, "Choose site", siteFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard originalSite) {
                            discount.addAnimationGroup(originalSite);
                            discount.allowResponses("Make Rebel scouts deploying from hand to " + GameUtils.getCardLink(originalSite) + " and related sites deploy -2",
                                    new RespondablePlayCardEffect(discount) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard site = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            final Filter sameAndRelated = Filters.or(Filters.sameCardId(site), Filters.relatedSite(site));
                                            discount.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(discount,
                                                            new DeployCostToLocationModifier(self, Filters.and(Filters.Rebel, Filters.scout, Zone.HAND), -2, sameAndRelated),
                                                            "Rebel scouts deploying from hand to same and related sites are deploy -2"));
                                            discount.appendEffect(
                                                    new AddUntilEndOfTurnActionProxyEffect(discount,
                                                            new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                    if (TriggerConditions.justDeployedFromHandToLocation(game, effectResult,
                                                                            Filters.and(Filters.Rebel, Filters.scout), sameAndRelated)) {
                                                                        PhysicalCard deployed = ((PlayCardResult) effectResult).getPlayedCard();
                                                                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, self.getCardId());
                                                                        action.setText("Prevent scout from battling or moving");
                                                                        action.appendEffect(
                                                                                new MayNotMoveOrBattleUntilEndOfTurnEffect(action, deployed));
                                                                        return Collections.singletonList(action);
                                                                    }
                                                                    return null;
                                                                }
                                                            }
                                                    ));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(discount);
        }

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__RAPID_DEPLOYMENT__DEPLOY_SCOUT;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction deploy = new PlayInterruptAction(game, self, gameTextActionId);
            deploy.setText("Deploy Rebel scout from Reserve Deck");
            deploy.allowResponses("Deploy a Rebel scout (deploy +2) to a site from Reserve Deck",
                    new RespondablePlayCardEffect(deploy) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            deploy.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(deploy,
                                            Filters.and(Filters.Rebel, Filters.scout), Filters.site, 2, true));
                        }
                    }
            );
            actions.add(deploy);
        }
        return actions;
    }
}
