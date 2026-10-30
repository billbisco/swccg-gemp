package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
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
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Interrupt
 * Subtype: Lost
 * Title: Return Of A Jedi
 */
public class Card1102_020 extends AbstractLostInterrupt {
    public Card1102_020() {
        super(Side.LIGHT, 3, Title.Return_Of_A_Jedi, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Where did you dig up that old fossil?' 'I don't think he exists anymore.' 'Surely he must be dead by now.' 'I can't believe he's gone.' 'Oh, he's not dead, not yet.' Obi's back!");
        setGameText("Use 1 Force to deploy (or retrieve) Obi-Wan, Obi-Wan's Cape, or Obi-Wan's Lightsaber from Reserve Deck; reshuffle. OR Unless Inner Strength on table, use 1 Force to cancel a just drawn weapon destiny targeting Obi-Wan (or a just drawn duel or lightsaber combat destiny if Obi-Wan is participating).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter uniqueFilter = Filters.or(Filters.ObiWan, Filters.title(Title.ObiWans_Cape), Filters.persona(Persona.OBIWANS_LIGHTSABER));
        GameTextActionId searchActionId = GameTextActionId.LEGACY__RETURN_OF_A_JEDI_V__DEPLOY_OR_RETRIEVE;

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, searchActionId, true, false)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, searchActionId);
            action.setText("Deploy from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Deploy Obi-Wan, Obi-Wan's Cape, or Obi-Wan's Lightsaber from Reserve Deck",
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
            action.allowResponses("Retrieve Obi-Wan, Obi-Wan's Cape, or Obi-Wan's Lightsaber",
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
        if (GameConditions.canSpot(game, self, Filters.title(Title.Inner_Strength))) {
            return null;
        }
        if (!GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                || !GameConditions.canCancelDestiny(game, playerId)) {
            return null;
        }

        boolean weaponTargetingObi = TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.ObiWan);
        boolean duelOrLightsaber = false;
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyType destinyType = ((DestinyDrawnResult) effectResult).getDestinyType();
            if (destinyType == DestinyType.DUEL_DESTINY
                    && GameConditions.canSpot(game, self, Filters.and(Filters.ObiWan, Filters.participatingInDuel))) {
                duelOrLightsaber = true;
            }
            if (destinyType == DestinyType.LIGHTSABER_COMBAT_DESTINY
                    && GameConditions.canSpot(game, self, Filters.and(Filters.ObiWan, Filters.participatingInLightsaberCombat))) {
                duelOrLightsaber = true;
            }
        }
        if (!weaponTargetingObi && !duelOrLightsaber) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Cancel destiny");
        action.appendCost(
                new UseForceEffect(action, playerId, 1));
        action.allowResponses("Cancel just drawn destiny",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new CancelDestinyEffect(action));
                    }
                }
        );
        return Collections.singletonList(action);
    }
}
