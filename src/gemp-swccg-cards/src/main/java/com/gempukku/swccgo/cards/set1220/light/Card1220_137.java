package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
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
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Attack Pattern Delta
 */
public class Card1220_137 extends AbstractLostInterrupt {
    public Card1220_137() {
        super(Side.LIGHT, 3, Title.Attack_Pattern_Delta, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Snowspeeder attack plan devised by Commander Skywalker and Rebel tactician Beryl Chifonage. Single-file formation protects the squadron as the leader draws fire.");
        setGameText("During a battle at a site, if you are about to draw a card for battle destiny, you may instead use the maneuver number of your participating combat vehicle.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final Filter combatVehicle = Filters.and(Filters.your(self), Filters.combat_vehicle, Filters.participatingInBattle, Filters.hasManeuverDefined);

        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canTarget(game, self, combatVehicle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Substitute destiny");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose combat vehicle", combatVehicle) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard vehicle) {
                            action.addAnimationGroup(vehicle);
                            float maneuver = game.getModifiersQuerying().getManeuver(game.getGameState(), vehicle);
                            action.allowResponses("Substitute " + GameUtils.getCardLink(vehicle) + "'s maneuver value of " + GuiUtils.formatAsString(maneuver) + " for battle destiny",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            float finalManeuver = game.getModifiersQuerying().getManeuver(game.getGameState(), finalTarget);
                                            action.appendEffect(
                                                    new SubstituteDestinyEffect(action, finalManeuver));
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
