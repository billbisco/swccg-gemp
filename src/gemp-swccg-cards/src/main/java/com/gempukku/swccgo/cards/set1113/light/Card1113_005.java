package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
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
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SetDestinyEvenIfCanceledEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Interrupt
 * Subtype: Used
 * Title: Darklighter Spin
 */
public class Card1113_005 extends AbstractUsedInterrupt {
    public Card1113_005() {
        super(Side.LIGHT, 3, Title.Darklighter_Spin, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLore("Biggs improvised this maneuver shortly after joining the Alliance. By spinning his starfighter while firing all four cannons, he was able to destroy multiple incoming targets.");
        setGameText("Deploy a Virtual Set 13 site from Reserve Deck; reshuffle. OR If opponent just modified your battle destiny draw, cancel and redraw it (and it may not be modified). OR If opponent just canceled your battle destiny draw, make that destiny = X instead, where X is the number of battlegrounds you occupy.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DARKLIGHTER_SPIN_V__DEPLOY_BATTLEGROUND_SYSTEM;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy a Virtual Set 13 site from Reserve Deck");
            action.allowResponses("Deploy a Virtual Set 13 site from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.site, new com.gempukku.swccgo.filters.Filter() {
                                        @Override
                                        public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                            return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_13;
                                        }
                                    }), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (effectResult.getType() != EffectResult.Type.DESTINY_DRAWN) {
            return actions;
        }
        DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
        if (destinyDrawnResult.getDestinyType() != DestinyType.BATTLE_DESTINY
                || !playerId.equals(effectResult.getPerformingPlayerId())) {
            return actions;
        }
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null) {
            return actions;
        }
        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
        if (drawDestinyEffect.getDestinyType() != DestinyType.BATTLE_DESTINY
                || !playerId.equals(drawDestinyEffect.getPlayerDrawingDestiny())) {
            return actions;
        }

        if (drawDestinyEffect.isDestinyCanceled()) {
            final int battlegroundsOccupied = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.battleground, Filters.occupies(playerId))).size();
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Set canceled battle destiny to " + battlegroundsOccupied);
            action.allowResponses("Make that destiny = " + battlegroundsOccupied,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            int x = Filters.filterTopLocationsOnTable(game,
                                    Filters.and(Filters.battleground, Filters.occupies(playerId))).size();
                            action.appendEffect(
                                    new SetDestinyEvenIfCanceledEffect(action, x));
                        }
                    }
            );
            actions.add(action);
            return actions;
        }

        boolean modified = Math.abs(drawDestinyEffect.getDestinyDrawValueModification()) > 0.001f;
        if (modified && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel and redraw battle destiny");
            action.allowResponses("Cancel and redraw battle destiny (it may not be modified)",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotModifyBattleDestinyModifier(self, playerId),
                                            "Battle destiny may not be modified"));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
