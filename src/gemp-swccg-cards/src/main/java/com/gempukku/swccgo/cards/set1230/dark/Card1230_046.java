package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Outflank
 */
public class Card1230_046 extends AbstractUsedInterrupt {
    public Card1230_046() {
        super(Side.DARK, 6, Title.Outflank, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Commander Igar's defense of Endor called for the use of speeder bikes to harass any attacking Rebels.");
        setGameText("If opponent just initiated battle at a site where their total power is greater than yours, you may use 1 Force (or cause opponent to use 1 Force) to relocate one of your characters or vehicles to that site from a related location.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.site)
                && GameConditions.isDuringBattleAt(game, Filters.site)) {
            PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(game.getGameState(), battleLocation, opponent, false, false);
            float yourPower = game.getModifiersQuerying().getTotalPowerAtLocation(game.getGameState(), battleLocation, playerId, false, false);
            if (theirPower > yourPower) {
                Filter relocatable = Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.vehicle), Filters.at(Filters.relatedLocationTo(self, Filters.sameCardId(battleLocation))));
                if (GameConditions.canTarget(game, self, relocatable)
                        && (GameConditions.canUseForce(game, playerId, 1) || GameConditions.canUseForce(game, opponent, 1))) {
                    final PhysicalCard site = battleLocation;
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Relocate character or vehicle to battle");
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose character or vehicle to relocate", relocatable) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                    action.addAnimationGroup(targetedCard);
                                    if (GameConditions.canUseForce(game, playerId, 1)) {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, 1));
                                    }
                                    else {
                                        action.appendCost(
                                                new UseForceEffect(action, opponent, 1));
                                    }
                                    action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to the battle",
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                    action.appendEffect(
                                                            new RelocateBetweenLocationsEffect(action, finalTarget, site));
                                                }
                                            }
                                    );
                                }
                            });
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
