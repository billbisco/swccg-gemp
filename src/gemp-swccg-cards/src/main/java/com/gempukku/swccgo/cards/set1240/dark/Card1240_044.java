package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.ReleaseWithRallyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Used Interrupt
 * Title: It's Worse
 */
public class Card1240_044 extends AbstractUsedInterrupt {
    public Card1240_044() {
        super(Side.DARK, 6, "It's Worse", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Things went from bad to worse when Luke was attacked by a dianoga, a parasitic predator native to the planet Vodran.");
        setGameText("Use 1 Force to deploy a pit from Reserve Deck; reshuffle. OR At end of your deploy phase, target a non-frozen captive at a site; captive is released and relocated to a related pit you do not occupy. OR During an attack, cancel a just drawn weapon destiny.");
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        GameTextActionId id = GameTextActionId.LEGACY__ITS_WORSE_V__DEPLOY_PIT;
        if (!GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            return null;
        }
        PlayInterruptAction action = new PlayInterruptAction(game, self, id);
        action.setText("Deploy a pit from Reserve Deck");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.pit, true));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(
            String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        if (TriggerConditions.isEndOfYourPhase(game, result, Phase.DEPLOY, playerId)) {
            return releaseToPit(playerId, game, self);
        }
        if (GameConditions.isDuringAttack(game) && TriggerConditions.isWeaponDestinyJustDrawn(game, result)
                && GameConditions.canCancelDestiny(game, playerId)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel weapon destiny during attack");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new CancelDestinyEffect(action));
                }
            });
            return List.of(action);
        }
        return null;
    }

    private static List<PlayInterruptAction> releaseToPit(String playerId, SwccgGame game, PhysicalCard self) {
        List<PhysicalCard> captives = Filters.filterAllOnTable(game,
                Filters.and(Filters.captive, Filters.not(Filters.frozenCaptive), Filters.at(Filters.site),
                        Filters.canBeTargetedBy(self, TargetingReason.TO_BE_RELEASED))).stream()
                .filter(card -> GameConditions.canSpotLocation(game, availablePits(game, playerId, card)))
                .toList();
        if (captives.isEmpty()) {
            return null;
        }

        PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Release captive to a related pit");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose non-frozen captive",
                TargetingReason.TO_BE_RELEASED, Filters.and(Filters.captive, Filters.in(captives))) {
            @Override
            protected void cardTargeted(int group, PhysicalCard captive) {
                action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose an unoccupied related pit",
                        availablePits(game, playerId, captive)) {
                    @Override
                    protected void cardSelected(PhysicalCard pit) {
                        action.addAnimationGroup(captive);
                        action.addAnimationGroup(pit);
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard target = action.getPrimaryTargetCard(group);
                                if (availablePits(game, playerId, target).accepts(game, pit)) {
                                    action.appendEffect(new ReleaseWithRallyEffect(action, target, pit, false));
                                }
                            }
                        });
                    }
                });
            }
        });
        return List.of(action);
    }

    private static Filter availablePits(SwccgGame game, String playerId, PhysicalCard captive) {
        if (captive == null || !captive.isCaptive() || captive.isFrozen()) {
            return Filters.none;
        }
        PhysicalCard site = game.getModifiersQuerying().getLocationHere(game.getGameState(), captive);
        if (site == null || !Filters.site.accepts(game, site)) {
            return Filters.none;
        }
        return Filters.and(Filters.site, Filters.pit, Filters.relatedSite(site),
                Filters.not(Filters.sameCardId(site)), Filters.not(Filters.occupies(playerId)));
    }
}
