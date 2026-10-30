package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Persona;
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
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Interrupt
 * Subtype: Lost
 * Title: Leia's Back
 */
public class Card1102_013 extends AbstractLostInterrupt {
    public Card1102_013() {
        super(Side.LIGHT, 3, Title.Leias_Back, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'It's not over yet!'");
        setGameText("Use 1 Force to deploy (or retrieve) Leia's Blaster Rifle, Leia's Sporting Blaster, or Leia from Reserve Deck; reshuffle. OR If you just initiated a Force drain where Leia is present, use 2 Force to make that Force drain +2.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter uniqueFilter = Filters.or(Filters.persona(Persona.LEIAS_BLASTER_RIFLE), Filters.title(Title.Leias_Sporting_Blaster), Filters.Leia);
        GameTextActionId searchActionId = GameTextActionId.LEGACY__LEIAS_BACK_V__DEPLOY_OR_RETRIEVE;

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, searchActionId, true, false)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, searchActionId);
            action.setText("Deploy from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Deploy Leia's Blaster Rifle, Leia's Sporting Blaster, or Leia from Reserve Deck",
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
            action.allowResponses("Retrieve Leia's Blaster Rifle, Leia's Sporting Blaster, or Leia",
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
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self, Filters.Leia))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 2 to Force drain");
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.allowResponses("Make that Force drain +2",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddToForceDrainEffect(action, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
