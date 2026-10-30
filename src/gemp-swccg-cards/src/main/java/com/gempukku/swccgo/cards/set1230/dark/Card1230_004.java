package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Igar
 */
public class Card1230_004 extends AbstractImperial {
    public Card1230_004() {
        super(Side.DARK, 1, 3, 3, 3, 6, Title.Igar, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leader. Served with General Veers' Blizzard unit at Battle of Hoth. Assigned by Vader to coordinate surface defense of Endor's moon. Kuat native.");
        setGameText("Adds 2 to power of anything he pilots, 3 to power of any combat vehicle. Once per turn, if piloting a combat vehicle, may use 1 Force to cancel a 'react' from a related site to this site or cancel an opponent's just drawn battle destiny at same site (except their first this battle).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.ENDOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1, Filters.combat_vehicle));
        return modifiers;
    }

    private boolean pilotingCombatVehicle(SwccgGame game, PhysicalCard self) {
        return Filters.piloting(Filters.combat_vehicle).accepts(game, self);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMMANDER_IGAR_V__CANCEL;
        GameState gameState = game.getGameState();

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && pilotingCombatVehicle(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && TriggerConditions.isMovingAsReact(game, effect, Filters.any)
                && effect instanceof com.gempukku.swccgo.logic.effects.MovingAsReactEffect
                && gameState.getMoveAsReactState() != null
                && gameState.getMoveAsReactState().getReactActionOption() != null) {
            PhysicalCard from = ((com.gempukku.swccgo.logic.effects.MovingAsReactEffect) effect).getMovingFrom();
            PhysicalCard here = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, self);
            if (from != null && here != null
                    && Filters.relatedSite(self).accepts(game, from)
                    && gameState.getMoveAsReactState().getReactActionOption().getTargetFilter().accepts(game, here)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel 'react'");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new CancelReactEffect(action));
            return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMMANDER_IGAR_V__CANCEL;

        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && pilotingCombatVehicle(game, self)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canCancelDestiny(game, playerId)
                && game.getGameState().getBattleState().getNumBattleDestinyDrawn(opponent) > 1) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel battle destiny");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
