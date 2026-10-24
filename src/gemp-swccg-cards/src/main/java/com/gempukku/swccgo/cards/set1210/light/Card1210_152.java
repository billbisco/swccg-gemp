package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Han's Back
 */
public class Card1210_152 extends AbstractLostInterrupt {
    public Card1210_152() {
        super(Side.LIGHT, 3, Title.Hans_Back, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Didn't we just leave this party?'");
        setGameText("Use 1 Force to deploy (or retrieve) Han's Heavy Blaster Pistol, Falcon, or Han from Reserve Deck; reshuffle. OR If opponent just drew weapon destiny targeting Falcon or Han, cancel and redraw that destiny.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter uniqueFilter = Filters.or(Filters.Hans_Heavy_Blaster_Pistol, Filters.Falcon, Filters.Han);
        GameTextActionId searchActionId = GameTextActionId.LEGACY__HANS_BACK_V__DEPLOY_OR_RETRIEVE;

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, searchActionId, true, false)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, searchActionId);
            action.setText("Deploy from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Deploy Han's Heavy Blaster Pistol, Falcon, or Han from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, uniqueFilter, true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSearchLostPile(game, playerId, self, searchActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, searchActionId);
            action.setText("Retrieve from Lost Pile");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Retrieve Han's Heavy Blaster Pistol, Falcon, or Han",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, uniqueFilter));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.or(Filters.Falcon, Filters.Han))
                && TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel and redraw weapon destiny");
            action.allowResponses("Cancel and redraw weapon destiny targeting Falcon or Han",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
