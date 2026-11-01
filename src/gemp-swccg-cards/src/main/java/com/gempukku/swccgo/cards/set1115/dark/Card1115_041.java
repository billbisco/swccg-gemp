package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.NoBattleDamageModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Interrupt
 * Subtype: Used
 * Title: Collateral Damage
 */
public class Card1115_041 extends AbstractUsedInterrupt {
    public Card1115_041() {
        super(Side.DARK, 4, Title.Collateral_Damage, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Blast vests and helmets with blast shields cannot protect Rebel soldiers from grenades, panel explosions and other combat hazards.");
        setGameText("If you just initiated battle where you have at least two characters (one a trooper and the other a leader), add two battle destiny. Opponent takes no battle damagethis battle. OR Deploy one Dewback from Reserve Deck; reshuffle.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.canSpot(game, self, 2, Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle))
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.trooper, Filters.participatingInBattle))
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.leader, Filters.participatingInBattle))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add two battle destiny");
            // Allow response(s)
            action.allowResponses("Add two battle destiny and make opponent take no battle damage",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 2));
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new NoBattleDamageModifier(self, Filters.any, opponent),
                                            "Opponent takes no battle damage"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COLLATERAL_DAMAGE_V__DEPLOY_DEWBACK;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy Dewback from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy a Dewback from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Dewback, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
